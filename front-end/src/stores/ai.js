import { defineStore } from 'pinia'
import { ref } from 'vue'
import { GoogleGenAI } from "@google/genai"
import { useUserStore } from './user'
import { usePomodoroStore } from './pomodoro'
import { getTasks } from '@/api/task'
import { getChatSessions, createChatSession, getChatMessages, saveChatMessage, deleteChatSession, updateChatSessionTitle } from '@/api/chat'

export const useAiStore = defineStore('ai', () => {
  const userStore = useUserStore()
  const pomodoroStore = usePomodoroStore()
  
  const sessions = ref([])
  const currentSessionId = ref(null)
  const messages = ref([])
  const isLoading = ref(false)
  const isSessionsLoading = ref(false)
  const currentModel = ref('gemini-3-flash-preview') 

  // Load all sessions for current user
  const loadSessions = async () => {
    if (!userStore.user?.id) return
    isSessionsLoading.value = true
    try {
      const data = await getChatSessions(userStore.user.id)
      sessions.value = data || []
    } catch (e) {
      console.error('Failed to load chat sessions', e)
    } finally {
      isSessionsLoading.value = false
    }
  }

  // Load a specific session's messages
  const selectSession = async (sessionId) => {
    currentSessionId.value = sessionId
    messages.value = []
    isLoading.value = true
    try {
      const data = await getChatMessages(sessionId)
      messages.value = (data || []).map(msg => ({
        id: msg.id,
        role: msg.role,
        content: msg.content,
        timestamp: new Date(msg.createdAt)
      }))
    } catch (e) {
      console.error('Failed to load messages', e)
    } finally {
      isLoading.value = false
    }
  }

  // Start a new chat session
  const startNewSession = () => {
    currentSessionId.value = null
    messages.value = []
  }

  const deleteSession = async (id) => {
    try {
      await deleteChatSession(id)
      sessions.value = sessions.value.filter(s => s.id !== id)
      if (currentSessionId.value === id) {
        startNewSession()
      }
    } catch (e) {
      console.error('Failed to delete session', e)
    }
  }

  const clearMessages = () => {
    messages.value = []
  }

  const generateSystemContext = async () => {
    const userName = userStore.user?.username || 'User'
    const focusTime = pomodoroStore.todayTotalMinutes || 0
    const currentMode = pomodoroStore.mode || 'Focus'
    
    let taskContext = "No specific tasks found."
    try {
      if (userStore.user?.id) {
        const tasks = await getTasks(userStore.user.id)
        if (tasks && tasks.length > 0) {
          const activeTasks = tasks.filter(t => t.status !== 'DONE').slice(0, 5)
          const doneTasks = tasks.filter(t => t.status === 'DONE').slice(0, 3)
          
          taskContext = `
Active Tasks (Top 5):
${activeTasks.map(t => `- [${t.priority}] ${t.title}`).join('\n')}

Recently Completed:
${doneTasks.map(t => `- ${t.title}`).join('\n')}
          `
        }
      }
    } catch (e) {
      console.error("Failed to fetch tasks for context", e)
    }

    return `
System: You are ACIR, an intelligent productivity assistant.
User: ${userName}
Focus Time Today: ${focusTime} minutes
Current Mode: ${currentMode}

Task Status:
${taskContext}

Guidelines:
- Be concise and encouraging.
- If focus time is high (>120 mins), suggest a break.
- Use Markdown.
- Style: Glassmorphism, transparent, intelligent.
    `
  }

  const sendMessage = async (userText) => {
    if (!userText.trim()) return

    // 1. Ensure we have a session
    if (!currentSessionId.value) {
      if (!userStore.user?.id) {
        messages.value.push({ role: 'model', content: 'Please login first.', isError: true, timestamp: new Date() })
        return
      }
      try {
        // Generate a short title from the first message
        const title = userText.length > 20 ? userText.substring(0, 20) + '...' : userText
        const newSession = await createChatSession({ userId: userStore.user.id, title })
        currentSessionId.value = newSession.id
        sessions.value.unshift(newSession) // Add to top of list
      } catch (e) {
        console.error('Failed to create session', e)
        return
      }
    }

    // 2. Save user message to backend
    try {
      await saveChatMessage(currentSessionId.value, { role: 'user', content: userText })
    } catch (e) {
      console.error('Failed to save user message', e)
    }

    // 3. Add User Message to State
    messages.value.push({
      role: 'user',
      content: userText,
      timestamp: new Date()
    })

    console.log('User Store Data:', userStore.user) // Debug
    const apiKey = userStore.user?.geminiApiKey || userStore.user?.gemini_api_key || import.meta.env.VITE_GEMINI_API_KEY
    if (!apiKey) {
      messages.value.push({
        role: 'model',
        content: "Please configure your Gemini API Key in **Profile Settings** first.",
        isError: true,
        timestamp: new Date()
      })
      return
    }

    const ai = new GoogleGenAI({ apiKey })

    isLoading.value = true

    try {
      // 2. Prepare Context
      const context = await generateSystemContext()
      
      // 3. Construct Prompt with History
      // We will send a structured prompt to the model
      // Note: For best results with chat, we should use the chat API if available, 
      // but 'contents' can be a list of messages.
      // The new SDK documentation suggests 'contents' can be a string or array.
      // Let's try to send the conversation history as a text block for now to ensure compatibility.
      
      let fullPrompt = `${context}\n\n--- Conversation History ---\n`
      
      messages.value.forEach(msg => {
        const role = msg.role === 'user' ? 'User' : 'ACIR'
        fullPrompt += `${role}: ${msg.content}\n`
      })
      
      // The last message is already in messages.value, so it's included above.
      // We need to prompt the model to answer.
      fullPrompt += `ACIR: `

      // 4. Call API
      const response = await ai.models.generateContent({
        model: currentModel.value,
        contents: fullPrompt,
        config: {
            temperature: 0.7
        }
      })
      
      // In @google/genai SDK, response.text is a property, not a function
      const responseText = response.text || "No response text"

      // Save AI message to backend
      try {
        await saveChatMessage(currentSessionId.value, { role: 'model', content: responseText })
      } catch (e) {
        console.error('Failed to save AI message', e)
      }

      // 5. Add AI Response to State
      messages.value.push({
        role: 'model',
        content: responseText,
        timestamp: new Date()
      })

    } catch (error) {
      console.error('AI Request Failed:', error)
      messages.value.push({
        role: 'model',
        content: "Sorry, I encountered an error. Please check your connection or API key.",
        isError: true,
        timestamp: new Date()
      })
    } finally {
      isLoading.value = false
    }
  }

  return {
    sessions,
    currentSessionId,
    messages,
    isLoading,
    isSessionsLoading,
    currentModel,
    loadSessions,
    selectSession,
    startNewSession,
    deleteSession,
    sendMessage,
    clearMessages
  }
})
