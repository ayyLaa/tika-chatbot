<template>
  <div class="h-screen w-full flex flex-col bg-[#F0F2F5] font-sans overflow-hidden">
    
    <!-- Header (Koyu Lacivert: #031B39) -->
    <header class="h-16 bg-[#031B39] px-6 flex items-center justify-between z-20 shrink-0 shadow-md">
      <!-- Sol: TİKA Logo -->
      <div class="flex items-center">
        <img 
          src="../assets/logo.png" 
          alt="TİKA AI Logo" 
          class="h-16 w-auto object-contain select-none cursor-pointer"
        />
      </div>

      <!-- Sağ: Share Button & Kullanıcı Profil Dropdown -->
      <div class="flex items-center gap-4">
        <!-- Paylaş (Share) Butonu -->
        <button 
          @click="showShareModal = true"
          class="flex items-center gap-1.5 text-[13px] font-medium text-[#031B39] hover:text-[#E30613] bg-[#EBF1F8] hover:bg-white px-3.5 py-1.5 rounded-lg transition shadow-2xs cursor-pointer"
          title="Share this chat with your department"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z" />
          </svg>
          <span>Share</span>
        </button>

        <!-- Profil Dropdown -->
        <div class="relative">
          <button 
            @click="isProfileOpen = !isProfileOpen"
            class="flex items-center gap-1.5 cursor-pointer focus:outline-none"
          >
            <span class="text-[14px] font-medium text-[#EBF1F8]">{{ currentUser.name }}</span>
            <svg class="w-4 h-4 text-[#EBF1F8]/80" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
            </svg>
          </button>

          <!-- Profil Menüsü -->
          <div 
            v-if="isProfileOpen" 
            class="absolute right-0 mt-2 w-64 bg-white border border-slate-200 rounded-xl shadow-xl py-2 z-50 text-xs"
          >
            <div class="px-4 py-2.5 border-b border-slate-100">
              <p class="font-bold text-[#031B39]">{{ currentUser.name }}</p>
              <p class="text-slate-500 truncate">{{ currentUser.email }}</p>
              <div class="mt-1.5 inline-block bg-[#EBF1F8] px-2 py-0.5 rounded text-[10px] text-[#E30613] font-semibold">
                {{ currentUser.departmentName }}
              </div>
            </div>
            <button 
              @click="logout" 
              class="w-full text-left px-4 py-2 text-[#E30613] hover:bg-red-50 font-medium flex items-center gap-2 cursor-pointer mt-1"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
              </svg>
              Sign Out
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- Ana Çalışma Alanı (Sidebar + Chat Area) -->
    <div class="flex flex-1 overflow-hidden">
      <!-- Sol Sidebar (Geçmiş ve Yeni Sohbet) -->
      <aside class="w-64 bg-[#EBF1F8]/50 border-r border-slate-200 flex flex-col p-4 shrink-0">
        <!-- New Chat Butonu -->
        <button 
          @click="startNewChat"
          class="w-full bg-[#E30613] hover:bg-[#c40510] text-white text-[14px] font-semibold py-2.5 px-4 rounded-lg flex items-center justify-between shadow-xs transition duration-200 mb-6 cursor-pointer"
        >
          <span>New chat</span>
          <span class="text-lg leading-none">+</span>
        </button>

        <!-- Geçmiş Listesi Başlığı -->
        <div class="text-[11px] font-bold text-[#031B39]/50 uppercase tracking-wider mb-3 px-1">
          HISTORY
        </div>

        <!-- Geçmiş Sohbetler -->
        <div class="flex-1 overflow-y-auto space-y-1">
          <button 
            v-for="(chat, index) in chatHistory" 
            :key="index"
            @click="selectHistoryChat(chat)"
            :class="[
              'w-full text-left text-[13px] px-3 py-2.5 rounded-lg truncate transition cursor-pointer',
              activeChatTitle === chat.title 
                ? 'bg-white text-[#E30613] font-semibold border-l-4 border-[#E30613] shadow-2xs' 
                : 'text-[#031B39] hover:bg-white/60'
            ]"
          >
            {{ chat.title }}
          </button>
        </div>
      </aside>

      <!-- Sağ Ana Sohbet Alanı -->
      <main class="flex-1 flex flex-col bg-[#F0F2F5] relative overflow-hidden">
        
        <!-- Karşılama Ekranı (Eğer Mesaj Yoksa) -->
        <div 
          v-if="messages.length === 0" 
          class="flex-1 flex flex-col items-center justify-center p-6 text-center"
        >
          <img
            src="../assets/ay-yildiz.png"
            alt="TİKA Ay Yıldız" 
            class="h-20 w-auto object-contain mb-0 mt-4 select-none"
          />

          <h1 class="text-3xl font-bold text-[#031B39] mb-2">
            Merhaba, {{ currentUser.firstName }}
          </h1>
          <p class="text-slate-600 text-[14px] max-w-md mb-8 leading-relaxed">
            Ask about TİKA programs, field procedures or reporting I'll help you find it.
          </p>

          <!-- Departmana Özel Dinamik Öneri Soruları -->
          <div class="flex flex-col gap-3 w-full max-w-lg">
            <button 
              v-for="(suggestion, i) in currentDepartmentPrompts" 
              :key="i"
              @click="sendSuggestedPrompt(suggestion)"
              class="w-full py-3 px-5 border border-[#E30613]/30 text-[#E30613] hover:bg-[#E30613] hover:text-white text-[13px] font-medium rounded-xl text-center transition duration-200 bg-white shadow-2xs cursor-pointer"
            >
              {{ suggestion }}
            </button>
          </div>
        </div>

        <!-- Sohbet Mesaj Akışı -->
        <div 
          v-else 
          class="flex-1 overflow-y-auto p-6 md:p-10 space-y-6 max-w-4xl mx-auto w-full"
        >
          <div 
            v-for="(msg, index) in messages" 
            :key="index"
            :class="[
              'flex w-full',
              msg.sender === 'user' ? 'justify-end' : 'justify-start'
            ]"
          >
            <!-- AI Mesajı -->
            <div v-if="msg.sender === 'ai'" class="flex gap-3 max-w-2xl items-start">
              <div class="w-8 h-8 rounded-full bg-[#E30613] text-white flex items-center justify-center font-bold text-xs shrink-0 mt-0.5 shadow-xs">
                AI
              </div>
              <div class="space-y-2">
                <div class="bg-[#EBF1F8] text-[#031B39] text-[14px] leading-relaxed p-4 rounded-2xl rounded-tl-none border border-slate-200/60 shadow-2xs">
                  {{ msg.text }}
                </div>
                <!-- Kaynak Doküman Gösterimi -->
                <div v-if="msg.sources && msg.sources.length" class="flex flex-wrap gap-1.5">
                  <span 
                    v-for="(src, sIdx) in msg.sources" 
                    :key="sIdx"
                    class="text-[10px] bg-white border border-slate-200 text-slate-600 px-2 py-0.5 rounded font-mono shadow-2xs"
                  >
                    📄 {{ src }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Kullanıcı Mesajı -->
            <div v-else class="max-w-xl">
              <div class="bg-[#E30613] text-white text-[14px] leading-relaxed p-3.5 px-4 rounded-2xl rounded-tr-none shadow-xs font-normal">
                {{ msg.text }}
              </div>
            </div>
          </div>
        </div>

        <!-- Alt Mesaj Yazma Alanı -->
        <div class="p-4 md:p-6 bg-[#F0F2F5] border-t border-slate-200/80 flex justify-center">
          <form 
            @submit.prevent="sendMessage" 
            class="max-w-4xl w-full flex items-center gap-3 bg-white border border-slate-300 rounded-xl px-4 py-2.5 shadow-xs focus-within:border-[#E30613] transition"
          >
            <input 
              v-model="inputMessage"
              type="text" 
              placeholder="Message TİKAI..."
              class="flex-1 bg-transparent text-[14px] focus:outline-none text-[#031B39] placeholder-slate-400"
            />
            <button 
              type="submit"
              :disabled="!inputMessage.trim() || isLoading"
              class="w-8 h-8 bg-[#E30613] hover:bg-[#c40510] disabled:opacity-40 text-white rounded-lg flex items-center justify-center transition shrink-0 cursor-pointer"
            >
              <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
              </svg>
            </button>
          </form>
        </div>
      </main>
    </div>

    <!-- Departman İçi Paylaşım Modalı -->
    <div 
      v-if="showShareModal" 
      class="fixed inset-0 bg-[#031B39]/50 backdrop-blur-xs flex items-center justify-center z-50 p-4"
    >
      <div class="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-100 relative">
        <div class="flex items-center justify-between mb-4 pb-3 border-b border-slate-100">
          <div>
            <h3 class="font-bold text-[#031B39] text-base">Share Chat with Department</h3>
            <p class="text-xs text-slate-500 mt-0.5">Only members in <b>{{ currentUser.departmentName }}</b> are listed.</p>
          </div>
          <button @click="showShareModal = false" class="text-slate-400 hover:text-slate-600 text-lg cursor-pointer">✕</button>
        </div>

        <div class="space-y-4">
          <div>
            <label class="block text-xs font-semibold text-[#031B39] mb-1">Select Colleague Email</label>
            <select 
              v-model="selectedColleagueEmail"
              class="w-full px-3.5 py-2.5 bg-[#F0F2F5] border border-slate-200 rounded-lg text-xs text-[#031B39] focus:outline-none focus:border-[#E30613] cursor-pointer"
            >
              <option value="" disabled selected>Select an email from your department</option>
              <option 
                v-for="colleague in departmentColleagues" 
                :key="colleague.email" 
                :value="colleague.email"
              >
                {{ colleague.name }} ({{ colleague.email }})
              </option>
            </select>
          </div>

          <div>
            <label class="block text-xs font-semibold text-[#031B39] mb-1">Optional Note</label>
            <textarea 
              v-model="shareNote"
              rows="2"
              placeholder="Add a brief note about this chat..."
              class="w-full px-3.5 py-2 bg-[#F0F2F5] border border-slate-200 rounded-lg text-xs text-[#031B39] focus:outline-none focus:border-[#E30613]"
            ></textarea>
          </div>

          <button 
            @click="sendShareEmail"
            :disabled="!selectedColleagueEmail || isSharing"
            class="w-full py-2.5 bg-[#E30613] hover:bg-[#c40510] disabled:opacity-50 text-white font-semibold text-xs rounded-lg shadow-xs transition flex items-center justify-center gap-2 cursor-pointer"
          >
            <span v-if="isSharing">Sending Email...</span>
            <span v-else>Send via Email →</span>
          </button>
        </div>

        <div v-if="shareSuccess" class="mt-3 p-3 bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs rounded-lg text-center font-medium">
          ✓ Full chat log successfully emailed to {{ selectedColleagueEmail }}!
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const currentUser = ref({
  name: 'Elif Yılmaz',
  firstName: 'Elif',
  email: 'elif.yilmaz@tika.gov.tr',
  departmentKey: 'it',
  departmentName: 'Department of Information Technology'
})

const departmentPrompts = {
  'it': [
    'How do I search TİKA internal data programs and databases?',
    'What are the cybersecurity guidelines for internal reporting?',
    'How do I request software access or IT technical support?',
    'Where can I find system maintenance and backup schedules?'
  ],
  'dis-iliskiler': [
    'What programs does TİKA run in Africa?',
    'How do we coordinate with local embassies and partner agencies?',
    'Where are TİKA\'s coordination offices located globally?',
    'What is the procedure for processing international grant requests?'
  ]
}

const currentDepartmentPrompts = computed(() => {
  const key = currentUser.value.departmentKey
  return departmentPrompts[key] || departmentPrompts['it']
})

const departmentColleagues = ref([
  { name: 'Safiye Alaca', email: 'safyealaca@gmail.com', departmentKey: 'it' },
  { name: 'Zeynep Demir', email: 'zeynep.demir@tika.gov.tr', departmentKey: 'it' },
  { name: 'Mehmet Öz', email: 'mehmet.oz@tika.gov.tr', departmentKey: 'it' },
  { name: 'Selin Şahin', email: 'selin.sahin@tika.gov.tr', departmentKey: 'it' }
])

const isProfileOpen = ref(false)
const showShareModal = ref(false)
const selectedColleagueEmail = ref('')
const shareNote = ref('')
const isSharing = ref(false)
const shareSuccess = ref(false)
const isLoading = ref(false)

const chatHistory = ref([
  {
    title: 'TİKA programs in Africa',
    messages: [
      { sender: 'user', text: 'What programs does TİKA run in Africa?' },
      { sender: 'ai', text: 'TİKA runs development cooperation programs across many African countries — covering health, education, agriculture, and infrastructure — delivered in coordination with local institutions and Turkish coordination offices in the region.' },
      { sender: 'user', text: 'Which country has the largest active program?' },
      { sender: 'ai', text: 'That varies by year and budget cycle — I can pull the current program list by country if you tell me which region you\'re reporting on.' }
    ]
  }
])

const activeChatTitle = ref('')
const messages = ref([])
const inputMessage = ref('')

const sendSuggestedPrompt = (promptText) => {
  inputMessage.value = promptText
  sendMessage()
}

const selectHistoryChat = (chat) => {
  activeChatTitle.value = chat.title
  if (chat.messages && chat.messages.length > 0) {
    messages.value = [...chat.messages]
  } else {
    messages.value = [
      { sender: 'user', text: chat.title },
      { sender: 'ai', text: 'Here are the internal data and guidelines regarding ' + chat.title + '.' }
    ]
  }
}

const startNewChat = () => {
  activeChatTitle.value = ''
  messages.value = []
}

// 1. CANLI FASTAPI CHAT ENTEGRASYONU (/api/chat)
const sendMessage = async () => {
  // inputMessage.value.trim() şeklinde .value eklendi
  if (!inputMessage.value || !inputMessage.value.trim() || isLoading.value) return

  const userText = inputMessage.value.trim()
  messages.value.push({ sender: 'user', text: userText })
  inputMessage.value = ''
  isLoading.value = true

  try {
    const response = await fetch('http://127.0.0.1:8000/api/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        message: userText,
        user_email: currentUser.value.email
      })
    })

    const data = await response.json()
    
    if (data.status === 'success') {
      messages.value.push({
        sender: 'ai',
        text: data.reply,
        sources: data.sources || []
      })
    } else {
      messages.value.push({
        sender: 'ai',
        text: 'Bir yanıt üretilirken hata oluştu.'
      })
    }
  } catch (error) {
    console.error('Chat API Hatası:', error)
    messages.value.push({
      sender: 'ai',
      text: 'FastAPI sunucusuna ulaşılamadı. Lütfen arka plan servisinin açık olduğunu kontrol edin.'
    })
  } finally {
    isLoading.value = false
  }
}

// 2. CANLI FASTAPI BİREBİR SOHBET E-POSTA PAYLAŞIM ENTEGRASYONU (/api/share)
const sendShareEmail = async () => {
  if (!selectedColleagueEmail.value) return

  isSharing.value = true

  // Ekrandaki Vue mesajlarını FastAPI'nin beklediği {role, content} yapısına dönüştür
  const formattedMessages = messages.value.map(msg => ({
    role: msg.sender === 'user' ? 'user' : 'assistant',
    content: msg.text
  }))

  try {
    const response = await fetch('http://localhost:8000/api/share', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        recipient_email: selectedColleagueEmail.value,
        messages: formattedMessages,
        note: shareNote.value,
        sender_name: currentUser.value.name
      })
    })

    const data = await response.json()

    if (data.status === 'success') {
      shareSuccess.value = true
      setTimeout(() => {
        shareSuccess.value = false
        showShareModal.value = false
        selectedColleagueEmail.value = ''
        shareNote.value = ''
      }, 2000)
    }
  } catch (error) {
    console.error('Paylaşım Hatası:', error)
    alert('E-posta gönderilirken bir sunucu hatası oluştu.')
  } finally {
    isSharing.value = false
  }
}

const logout = () => {
  router.push('/login')
}
</script>