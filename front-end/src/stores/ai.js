import { defineStore } from 'pinia'
import { ref } from 'vue'
import { GoogleGenAI } from "@google/genai"
import { useUserStore } from './user'
import { usePomodoroStore } from './pomodoro'
import { getTasks } from '@/api/task'

export const useAiStore = defineStore('ai', () => {
  const userStore = useUserStore()
  const pomodoroStore = usePomodoroStore()
  
  const messages = ref([])
  const isLoading = ref(false)
  const currentModel = ref('gemini-3-flash-preview') 

  const apiKey = import.meta.env.VITE_GEMINI_API_KEY
  // Initialize the client
  const ai = new GoogleGenAI({ apiKey })

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

    // 1. Add User Message to State
    messages.value.push({
      role: 'user',
      content: userText,
      timestamp: new Date()
    })

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
    messages,
    isLoading,
    currentModel,
    sendMessage,
    clearMessages
  }
})
