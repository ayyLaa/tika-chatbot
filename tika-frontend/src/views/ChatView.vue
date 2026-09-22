<template>
  <div class="h-screen w-full flex flex-col bg-[#F0F2F5] font-sans overflow-hidden">

    <!-- Header -->
    <header class="h-16 bg-[#031B39] px-6 flex items-center justify-between z-20 shrink-0 shadow-md">
      <div class="flex items-center">
        <img src="../assets/logo.png" alt="TİKA AI Logo" class="h-16 w-auto object-contain select-none cursor-pointer" />
      </div>

      <div class="flex items-center gap-4">
        <button @click="showShareModal = true" class="flex items-center gap-1.5 text-[13px] font-medium text-[#031B39] hover:text-[#E30613] bg-[#EBF1F8] hover:bg-white px-3.5 py-1.5 rounded-lg transition shadow-2xs cursor-pointer" title="Bu sohbeti departmanınızla paylaşın">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z" /></svg>
          <span>Paylaş</span>
        </button>

        <div class="relative">
          <button @click="isProfileOpen = !isProfileOpen" class="flex items-center gap-1.5 cursor-pointer focus:outline-none">
            <span class="text-[14px] font-medium text-[#EBF1F8]">{{ currentUser.name }}</span>
            <svg class="w-4 h-4 text-[#EBF1F8]/80" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/></svg>
          </button>

          <div v-if="isProfileOpen" class="absolute right-0 mt-2 w-64 bg-white border border-slate-200 rounded-xl shadow-xl py-2 z-50 text-xs">
            <div class="px-4 py-2.5 border-b border-slate-100">
              <p class="font-bold text-[#031B39]">{{ currentUser.name }}</p>
              <p class="text-slate-500 truncate">{{ currentUser.email }}</p>
              <div class="mt-1.5 inline-block bg-[#EBF1F8] px-2 py-0.5 rounded text-[10px] text-[#E30613] font-semibold">{{ currentUser.departmentName }}</div>
            </div>
            <button
                v-if="currentUser.role && currentUser.role.toUpperCase().includes('ADMIN')"
                @click="$router.push('/admin')"
                class="w-full text-left px-4 py-2 text-[#031B39] hover:bg-slate-50 font-medium flex items-center gap-2 cursor-pointer mt-1 border-b border-slate-100"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-[#031B39]" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              Yönetim Konsolu
            </button>
            <button @click="logout" class="w-full text-left px-4 py-2 text-[#E30613] hover:bg-red-50 font-medium flex items-center gap-2 cursor-pointer mt-1">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" /></svg>
              Çıkış Yap
            </button>
          </div>
        </div>
      </div>
    </header>

    <div class="flex flex-1 overflow-hidden">
      <!-- Left Sidebar -->
      <aside class="w-64 bg-[#EBF1F8]/50 border-r border-slate-200 flex flex-col p-4 shrink-0">
        <button @click="startNewChat" class="w-full bg-[#E30613] hover:bg-[#c40510] text-white text-[14px] font-semibold py-2.5 px-4 rounded-lg flex items-center justify-between shadow-xs transition duration-200 mb-6 cursor-pointer">
          <span>Yeni sohbet</span><span class="text-lg leading-none">+</span>
        </button>

        <div class="text-[11px] font-bold text-[#031B39]/50 uppercase tracking-wider mb-3 px-1">GEÇMİŞ</div>

        <div class="flex-1 overflow-y-auto space-y-1">
          <button v-for="chat in chatHistory" :key="chat.sessionId" @click="selectHistoryChat(chat)" :class="['w-full text-left text-[13px] px-3 py-2.5 rounded-lg truncate transition cursor-pointer', activeSessionId === chat.sessionId ? 'bg-white text-[#E30613] font-semibold border-l-4 border-[#E30613] shadow-2xs' : 'text-[#031B39] hover:bg-white/60']">
            {{ chat.title }}
          </button>
        </div>
      </aside>

      <!-- Right Main Chat Area -->
      <main class="flex-1 flex flex-col bg-[#F0F2F5] relative overflow-hidden">
        <div v-if="messages.length === 0" class="flex-1 flex flex-col items-center justify-center p-6 text-center">
          <img src="../assets/ay-yildiz.png" alt="TİKA Ay Yıldız" class="h-20 w-auto object-contain mb-0 mt-4 select-none" />
          <h1 class="text-3xl font-bold text-[#031B39] mb-2">Merhaba, {{ currentUser.firstName }}</h1>
          <p class="text-slate-600 text-[14px] max-w-md mb-8 leading-relaxed">TİKA programları, saha prosedürleri veya raporlama hakkında sorun, bulmanıza yardımcı olayım.</p>
          <div class="flex flex-col gap-3 w-full max-w-lg">
            <button v-for="(suggestion, i) in currentDepartmentPrompts" :key="i" @click="sendSuggestedPrompt(suggestion)" class="w-full py-3 px-5 border border-[#E30613]/30 text-[#E30613] hover:bg-[#E30613] hover:text-white text-[13px] font-medium rounded-xl text-center transition duration-200 bg-white shadow-2xs cursor-pointer">
              {{ suggestion }}
            </button>
          </div>
        </div>

        <div v-else class="flex-1 overflow-y-auto p-6 md:p-10 space-y-6 max-w-4xl mx-auto w-full" id="chat-container">
          <div v-for="(msg, index) in messages" :key="index" :class="['flex w-full', msg.sender === 'user' ? 'justify-end' : 'justify-start']">

            <!-- AI Message -->
            <div v-if="msg.sender === 'ai'" class="flex gap-3 max-w-2xl items-start">
              <div class="w-8 h-8 rounded-full bg-[#E30613] text-white flex items-center justify-center font-bold text-xs shrink-0 mt-0.5 shadow-xs">AI</div>
              <div class="space-y-2 w-full">
                <div class="bg-[#EBF1F8] text-[#031B39] text-[14px] leading-relaxed p-4 rounded-2xl rounded-tl-none border border-slate-200/60 shadow-2xs whitespace-pre-wrap">
                  {{ msg.text }}
                </div>

                <!-- FEEDBACK (Like / Dislike Area) -->
                <div v-if="!isReadOnly" class="pt-2 flex items-center justify-between text-xs text-slate-500 px-1">
                  <span v-if="msg.feedbackGiven" class="text-[11px] font-semibold text-emerald-600">✓ Geri bildiriminiz için teşekkürler!</span>
                  <template v-else>
                    <span class="text-[11px] font-medium text-slate-400">Bu yanıt faydalı oldu mu?</span>
                    <div class="flex items-center space-x-2">
                      <button @click="sendFeedback(msg, 'LIKE')" class="p-1 hover:bg-slate-200 rounded transition cursor-pointer text-slate-600 hover:text-green-600" title="Faydalı">👍</button>
                      <button @click="msg.showFeedbackInput = !msg.showFeedbackInput" class="p-1 hover:bg-slate-200 rounded transition cursor-pointer text-slate-600 hover:text-red-600" title="Geliştirilmeli">👎</button>
                    </div>
                  </template>
                </div>

                <!-- Small Text Box Opened When Disliked -->
                <div v-if="!isReadOnly && msg.showFeedbackInput && !msg.feedbackGiven" class="pt-2 space-y-2 bg-white p-3 rounded-xl border border-slate-200 shadow-2xs">
                  <input
                      v-model="msg.feedbackText"
                      type="text"
                      placeholder="Neden memnun kalmadığınızı belirtebilirsiniz (isteğe bağlı)..."
                      class="w-full bg-[#F0F2F5] border border-slate-200 rounded-lg px-3 py-1.5 text-xs text-slate-800 focus:outline-none focus:border-[#E30613]"
                  />
                  <div class="flex justify-end space-x-2">
                    <button @click="msg.showFeedbackInput = false" class="px-2.5 py-1 text-[10px] font-semibold text-slate-500 hover:bg-slate-100 rounded cursor-pointer">İptal</button>
                    <button @click="sendDetailedFeedback(msg)" class="px-2.5 py-1 text-[10px] font-bold bg-[#E30613] text-white rounded-lg hover:bg-[#c40510] cursor-pointer">Gönder</button>
                  </div>
                </div>
              </div>
            </div>

            <!-- User Message -->
            <div v-else class="max-w-xl">
              <div class="bg-[#E30613] text-white text-[14px] leading-relaxed p-3.5 px-4 rounded-2xl rounded-tr-none shadow-xs font-normal">{{ msg.text }}</div>
            </div>
          </div>

          <!-- Loading (Typing) Animation -->
          <div v-if="isLoading" class="flex gap-3 max-w-2xl items-start mt-4">
            <div class="w-8 h-8 rounded-full bg-[#E30613] text-white flex items-center justify-center font-bold text-xs shrink-0 shadow-xs">AI</div>
            <div class="bg-[#EBF1F8] p-4 rounded-2xl rounded-tl-none border border-slate-200/60 shadow-2xs flex space-x-1.5 items-center h-10">
              <div class="w-2 h-2 bg-slate-400 rounded-full animate-bounce" style="animation-delay: 0ms"></div>
              <div class="w-2 h-2 bg-slate-400 rounded-full animate-bounce" style="animation-delay: 150ms"></div>
              <div class="w-2 h-2 bg-slate-400 rounded-full animate-bounce" style="animation-delay: 300ms"></div>
            </div>
          </div>
        </div>

        <div v-if="!isReadOnly" class="p-4 md:p-6 bg-[#F0F2F5] border-t border-slate-200/80 flex justify-center">
          <form @submit.prevent="sendMessage" class="max-w-4xl w-full flex items-center gap-3 bg-white border border-slate-300 rounded-xl px-4 py-2.5 shadow-xs focus-within:border-[#E30613] transition">
            <input v-model="inputMessage" type="text" placeholder="TİKAI'ye mesaj yazın..." class="flex-1 bg-transparent text-[14px] focus:outline-none text-[#031B39] placeholder-slate-400" />
            <button type="submit" :disabled="!inputMessage.trim() || isLoading" class="w-8 h-8 bg-[#E30613] hover:bg-[#c40510] disabled:opacity-40 text-white rounded-lg flex items-center justify-center transition shrink-0 cursor-pointer">
              <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24"><path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/></svg>
            </button>
          </form>
        </div>
        <div v-else class="p-4 bg-amber-50 border-t border-amber-200 text-amber-800 text-xs text-center font-medium">
          👁️ Bu sohbet sizinle paylaşılmıştır (Salt Okunur / Read-Only Modu). Yeni mesaj gönderilemez.
        </div>
      </main>
    </div>

    <!-- Modals -->
    <div v-if="showShareModal" class="fixed inset-0 bg-[#031B39]/50 backdrop-blur-xs flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-100 relative">
        <div class="flex items-center justify-between mb-4 pb-3 border-b border-slate-100">
          <div><h3 class="font-bold text-[#031B39] text-base">Share Chat</h3></div>
          <button @click="showShareModal = false" class="text-slate-400 hover:text-slate-600 text-lg cursor-pointer">✕</button>
        </div>
        <div class="space-y-4">
          <div>
            <label class="block text-xs font-semibold text-[#031B39] mb-1">Select Colleague Email</label>
            <select v-model="selectedColleagueEmail" class="w-full px-3.5 py-2.5 bg-[#F0F2F5] border border-slate-200 rounded-lg text-xs text-[#031B39] focus:outline-none focus:border-[#E30613] cursor-pointer">
              <option value="" disabled selected>Departman arkadaşı seçin...</option>
              <option v-for="colleague in departmentColleagues" :key="colleague.email" :value="colleague.email">
                {{ colleague.name }} (Read-Only Erişim)
              </option>
            </select>
          </div>
          <div>
            <label class="block text-xs font-semibold text-[#031B39] mb-1">Note</label>
            <textarea v-model="shareNote" rows="2" class="w-full px-3.5 py-2 bg-[#F0F2F5] border border-slate-200 rounded-lg text-xs text-[#031B39] focus:outline-none focus:border-[#E30613]"></textarea>
          </div>
          <button @click="sendShareEmail" :disabled="!selectedColleagueEmail || isSharing" class="w-full py-2.5 bg-[#E30613] hover:bg-[#c40510] disabled:opacity-50 text-white font-semibold text-xs rounded-lg shadow-xs transition flex items-center justify-center gap-2 cursor-pointer">
            <span v-if="isSharing">Sending...</span><span v-else>Send via Email →</span>
          </button>
        </div>
        <div v-if="shareSuccess" class="mt-3 p-3 bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs rounded-lg text-center font-medium">✓ Sent successfully!</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useToast } from '../composables/useToast'

const router = useRouter()
const route = useRoute()
const { showToast } = useToast()

const isReadOnly = ref(false)

const currentUser = ref({ name: '', firstName: '', email: '', role: '', departmentKey: 'it', departmentName: 'Department of Information Technology' })

const authFetch = async (url, options = {}) => {
  const response = await fetch(url, {
    ...options,
    headers: {
      ...(options.headers || {}),
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  })
  if (response.status === 401) {
    localStorage.removeItem('token')
    localStorage.removeItem('userEmail')
    localStorage.setItem('redirectUrl', route.fullPath)
    router.push('/login')
    throw new Error('Token istekao')
  }
  return response
}

const fetchChatHistory = async () => {
  try {
    const response = await authFetch('http://localhost:8080/api/chat/history')
    if (response.ok) {
      const historyData = await response.json()
      chatHistory.value = historyData.map(session => {
        const mappedMessages = []
        session.messages.forEach(m => {
          mappedMessages.push({ sender: 'user', text: m.question })
          if (m.answer) {
            mappedMessages.push({
              id: m.id,
              sender: 'ai',
              text: m.answer,
              sources: m.sources || [],
              showFeedbackInput: false,
              feedbackText: '',
              feedbackGiven: false
            })
          }
        })
        return { sessionId: session.sessionId, title: session.title, messages: mappedMessages }
      })
    }
  } catch (error) {
    console.warn('Historija se nije mogla učitati:', error)
  }
}

const fetchColleagues = async () => {
  try {
    const response = await authFetch('http://localhost:8080/api/users/colleagues')
    if (response.ok) {
      const data = await response.json()
      console.log("Podaci iz Jave (UserSummaryDto):", data)

      departmentColleagues.value = data.map(u => ({
        name: u.fullName || u.username || u.email,
        email: u.email
      }))
    }
  } catch (error) {
    console.warn('Kolege se nisu mogle učitati:', error)
  }
}

// Izdvojena logika za učitavanje chata iz ?session_id= query parametra.
// Poziva se i iz onMounted (prvi ulazak na stranicu) i iz watch-a ispod
// (kad se query promijeni dok je komponenta već mount-ovana, npr. klik na share link
// dok si već na /chat — Vue Router ne remontira komponentu samo zbog promjene query-ja).
const loadSessionFromQuery = async (sharedSessionId) => {
  if (!sharedSessionId) return

  const myChat = chatHistory.value.find(c => c.sessionId === sharedSessionId)

  if (myChat) {
    // SLUČAJ 1: Ovo je chat trenutnog korisnika (vlasnik je kliknuo svoj link ili ga ima u historiji)
    selectHistoryChat(myChat)
    isReadOnly.value = false
  } else {
    // SLUČAJ 2: Ovo je tuđi chat (poslan mu je preko share-a). Pitamo Javu da li ima pravo pristupa!
    try {
      const resp = await authFetch(`http://localhost:8080/api/chat/shared/${sharedSessionId}`)
      const data = await resp.json()

      if (data.status === 'success') {
        activeSessionId.value = sharedSessionId
        messages.value = data.messages
        currentSessionId.value = sharedSessionId
        isReadOnly.value = true
        scrollToBottom()
      } else {
        // Ako email nije onaj kome je poslato, Java vraća grešku
        showToast(data.message || 'Bu sohbete erişim yetkiniz yok.', 'error')
        router.replace('/chat')
      }
    } catch (e) {
      console.error("Paylaşılan sohbet yüklenemedi:", e)
      showToast('Paylaşılan sohbet yüklenemedi.', 'error')
    }
  }
}

onMounted(async () => {
  const token = localStorage.getItem('token')

  if (!token) {
    localStorage.setItem('redirectUrl', route.fullPath)
    router.push('/login')
    return
  }

  let userRole = ''
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    userRole = payload.role || payload.roles || payload.authorities || ''
  } catch (e) {
    console.warn('Token decodiranje nije uspjelo:', e)
  }

  const email = localStorage.getItem('userEmail') || ''
  const namePart = email.split('@')[0].replace('.', ' ')
  const displayName = namePart.split(' ').map(w => w.charAt(0).toUpperCase() + w.slice(1)).join(' ')

  currentUser.value = { ...currentUser.value, name: displayName || email, firstName: displayName.split(' ')[0] || email, email: email }

  currentUser.value = {
    ...currentUser.value,
    name: displayName || email,
    firstName: displayName.split(' ')[0] || email,
    email: email,
    role: userRole
  }
  await fetchChatHistory()
  await fetchColleagues()

  await loadSessionFromQuery(route.query.session_id)
})

// Ako se query (session_id) promijeni dok je ChatView već mount-ovan
// (npr. klik na share link dok si već na /chat), ovo pokreće isto učitavanje.
watch(() => route.query.session_id, (newId) => {
  if (newId) loadSessionFromQuery(newId)
})

const departmentPrompts = {
  'it': [
    'TİKA\'nın merkez ofisi nerede bulunuyor?',
    'TİKA hangi ülkelerde ofis bulunduruyor?',
    'TİKA\'nın başkanı kim?',
    'TİKA\'nın teşkilat şeması nasıldır?'
  ],
  'dis-iliskiler': [
    'TİKA\'nın bakan yardımcısı kim?',
    'TİKA hangi ülkelerde program koordinasyon ofisi bulunduruyor?',
    'TİKA\'nın vizyonu ve misyonu nedir?',
    'TİKA staj başvuru şartları nelerdir?'
  ]
}

const currentDepartmentPrompts = computed(() => departmentPrompts[currentUser.value.departmentKey] || departmentPrompts['it'])

const departmentColleagues = ref([])
const isProfileOpen = ref(false)
const showShareModal = ref(false)
const selectedColleagueEmail = ref('')
const shareNote = ref('')
const isSharing = ref(false)
const shareSuccess = ref(false)
const isLoading = ref(false)
const currentSessionId = ref(null)

const chatHistory = ref([])
const activeSessionId = ref(null)
const messages = ref([])
const inputMessage = ref('')

// Naslov trenutno aktivnog chata — koristi se za share modal, pošto activeChatTitle više ne postoji.
const currentChatTitle = computed(() => {
  const found = chatHistory.value.find(c => c.sessionId === activeSessionId.value)
  return found ? found.title : 'Paylaşılan Sohbet'
})

const scrollToBottom = () => {
  nextTick(() => {
    const container = document.getElementById('chat-container')
    if (container) container.scrollTop = container.scrollHeight
  })
}

const sendSuggestedPrompt = (promptText) => {
  inputMessage.value = promptText
  sendMessage()
}

const selectHistoryChat = (chat) => {
  activeSessionId.value = chat.sessionId
  messages.value = [...chat.messages]
  currentSessionId.value = chat.sessionId
  isReadOnly.value = false
  scrollToBottom()
}

const startNewChat = () => {
  activeSessionId.value = null
  messages.value = []
  currentSessionId.value = null
  isReadOnly.value = false
  router.replace('/chat')
}

const sendMessage = async () => {
  if (!inputMessage.value || !inputMessage.value.trim() || isLoading.value) return

  const userText = inputMessage.value.trim()
  messages.value.push({ sender: 'user', text: userText })
  inputMessage.value = ''
  isLoading.value = true
  scrollToBottom()

  try {
    const response = await authFetch('http://localhost:8080/api/chat/ask', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ question: userText, sessionId: currentSessionId.value })
    })

    if (!response.ok) {
      messages.value.push({ sender: 'ai', text: 'Sunucuya ulaşılamadı. Lütfen tekrar deneyin.', showFeedbackInput: false, feedbackText: '', feedbackGiven: false })
      return
    }
    const data = await response.json()
    currentSessionId.value = data.sessionId
    activeSessionId.value = data.sessionId

    messages.value.push({
      id: data.messageId,
      sender: 'ai',
      text: data.answer,
      sources: data.sources || [],
      showFeedbackInput: false,
      feedbackText: '',
      feedbackGiven: false
    })

    const existingChat = chatHistory.value.find(c => c.sessionId === data.sessionId)
    if (!existingChat) {
      chatHistory.value.unshift({
        title: userText.length > 30 ? userText.substring(0, 30) + '...' : userText,
        sessionId: data.sessionId,
        messages: messages.value
      })
    }
  } catch (error) {
    messages.value.push({ sender: 'ai', text: 'Sunucuya ulaşılamadı. Lütfen servisleri kontrol edin.', showFeedbackInput: false, feedbackText: '', feedbackGiven: false })
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}

// Geri bildirim gönderme fonksiyonu
const sendFeedback = async (msg, type, text = '') => {
  msg.feedbackGiven = true
  msg.showFeedbackInput = false
  try {
    await authFetch('http://localhost:8080/api/chat/feedback', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        messageId: msg.id,
        rating: type === 'LIKE' ? 1 : 0,
        comment: text
      })
    })
  } catch (error) {
    console.error('Feedback gönderilemedi:', error)
  }
}

const sendDetailedFeedback = (msg) => {
  sendFeedback(msg, 'DISLIKE', msg.feedbackText)
}

const sendShareEmail = async () => {
  if (!selectedColleagueEmail.value || !currentSessionId.value) {
    showToast('Lütfen önce bir sohbet seçin ve arkadaşınızı belirleyin.', 'error')
    return
  }
  isSharing.value = true

  try {
    const response = await authFetch('http://localhost:8080/api/chat/share-in-app', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        session_id: currentSessionId.value,
        session_title: currentChatTitle.value,
        recipient_email: selectedColleagueEmail.value,
        sender_name: currentUser.value.name,
        note: shareNote.value
      })
    })
    const data = await response.json()
    if (data.status === 'success') {
      shareSuccess.value = true
      setTimeout(() => {
        shareSuccess.value = false;
        showShareModal.value = false;
        selectedColleagueEmail.value = '';
        shareNote.value = ''
      }, 2000)
    } else {
      showToast(data?.message || 'Paylaşım başarısız oldu.', 'error')
    }
  } catch (error) {
    showToast('Paylaşım sırasında hata oluştu.', 'error')
  } finally {
    isSharing.value = false
  }
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userEmail')
  router.push('/login')
}
</script>