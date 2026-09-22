<template>
  <div class="h-screen w-full flex bg-[#F4F5F7] font-sans overflow-hidden">
    <!-- Left Side: Vivid Red Brand Area -->
    <div class="hidden lg:flex lg:w-2/5 bg-tika-red p-10 flex-col justify-between relative overflow-hidden h-full">
      <!-- TİKA Logo with Crescent and Star -->
      <TikaLogo />

      <!-- Slogan Area -->
      <div class="my-auto max-w-lg z-10 space-y-0 mt-26">
        <img
            src="../assets/slogan.png"
            alt="Türk Halkının Kalbinden"
            class="w-full max-w-[400px] h-auto object-contain select-none"
        />

        <!-- Description Text -->
        <p class="text-white/90 text-[13px] leading-[1.6] font-normal relative -top-14">
          Bu yapay zeka asistanı, TİKA personelinin kurum içi veri programlarını, prosedürleri ve raporları tek seferde bir soru sorarak araştırmasına yardımcı olmak üzere tasarlanmıştır.
        </p>
      </div>

      <!-- Footer Info -->
      <div class="text-[10px] text-[#EBF1F8]">
        © 2026 TİKA — Türk İşbirliği ve Koordinasyon Ajansı
      </div>
    </div>

    <!-- Right Side: Form Area -->
    <div class="w-full lg:w-3/5 flex items-center justify-center p-6 sm:p-10 h-full overflow-y-auto lg:overflow-y-hidden">
      <!-- Wide card proportional to full screen -->
      <div class="w-full max-w-3xl bg-white p-8 sm:p-10 rounded-2xl shadow-xs border border-slate-100">

        <!-- SUCCESSFUL REGISTRATION — visible card instead of form -->
        <div v-if="signupSuccess" class="text-center py-8 space-y-4">
          <div class="w-14 h-14 mx-auto rounded-full bg-emerald-50 flex items-center justify-center">
            <svg class="w-7 h-7 text-emerald-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <div>
            <h2 class="text-xl font-bold text-tika-dark mb-1">Hesabınız Oluşturuldu!</h2>
            <p class="text-footer-muted text-[13px]">Artık TİKA dahili asistanına giriş yapabilirsiniz.</p>
          </div>
          <button @click="goToLogin" class="mt-2 py-2.5 px-8 bg-tika-red hover:bg-tika-redHover text-white text-[14px] font-semibold rounded-lg shadow-xs transition-colors duration-200 cursor-pointer">
            Giriş Yap →
          </button>
        </div>

        <!-- ALL EXISTING -->
        <template v-else>
          <!-- Tab Switch (Sign Up only visible if there's an Invite Token, and not in Forgot Password mode) -->
          <div v-if="hasInviteToken && !isForgotPassword" class="flex gap-2 p-1 bg-tika-bubbleBot rounded-lg mb-6">
            <button
                @click="isLogin = true"
                :class="[
                'flex-1 py-2 text-[14px] font-semibold rounded-md transition-all duration-200 cursor-pointer',
                isLogin ? 'bg-tika-red text-white shadow-xs' : 'text-tika-textMuted hover:text-tika-dark'
              ]"
            >
              Giriş Yap
            </button>
            <button
                @click="isLogin = false"
                :class="[
                'flex-1 py-2 text-[14px] font-semibold rounded-md transition-all duration-200 cursor-pointer',
                !isLogin ? 'bg-tika-red text-white shadow-xs' : 'text-tika-textMuted hover:text-tika-dark'
              ]"
            >
              Kayıt Ol
            </button>
          </div>

          <!-- Headings -->
          <div class="mb-5">
            <h2 class="text-xl font-bold mb-1 text-tika-dark">
              {{ isForgotPassword ? 'Şifre Sıfırlama' : (isLogin ? 'Tekrar Hoş Geldiniz' : 'Hesabınızı Oluşturun') }}
            </h2>
            <p class="text-footer-muted text-[13px]">
              {{ isForgotPassword ? 'Şifre sıfırlama talebinde bulunmak için kurumsal e-posta adresinizi girin.' : (isLogin ? 'TİKA dahili yapay zeka asistanına tam erişim sağlayın.' : 'Kurumsal davetiye ile kaydınızı tamamlayın.') }}
            </p>
          </div>

          <!-- Form (Login / Sign Up) -->
          <form v-if="!isForgotPassword" @submit.prevent="handleAuth" class="space-y-3">
            <!-- Full Name (Sign Up only) -->
            <div v-if="!isLogin" class="space-y-1">
              <label class="text-[12px] font-semibold text-tika-dark">Ad Soyad</label>
              <input
                  v-model="form.name"
                  type="text"
                  placeholder="Örn. Elif Yılmaz"
                  required
                  class="w-full px-3.5 py-2 bg-tika-bubbleBot border border-transparent rounded-lg text-body-chat focus:outline-none focus:border-tika-red focus:bg-white transition"
              />
            </div>

            <!-- Email -->
            <div class="space-y-1">
              <label class="text-[12px] font-semibold text-tika-dark">E-posta</label>
              <input
                  v-model="form.email"
                  type="text"
                  placeholder="örn. isim@tika.gov.tr"
                  required
                  class="w-full px-3.5 py-2 bg-tika-bubbleBot border border-transparent rounded-lg text-body-chat focus:outline-none focus:border-tika-red focus:bg-white transition"
              />
            </div>

            <!-- Password -->
            <div class="space-y-1">
              <div class="flex justify-between items-center">
                <label class="text-[12px] font-semibold text-tika-dark">Şifre</label>
                <!-- Forgot Password Link (only visible on Login screen) -->
                <button
                    v-if="isLogin"
                    type="button"
                    @click="isForgotPassword = true"
                    class="text-[11px] text-tika-red hover:underline cursor-pointer font-medium"
                >
                  Şifrenizi mi unuttunuz?
                </button>
              </div>
              <input
                  v-model="form.password"
                  type="password"
                  placeholder="••••••••"
                  required
                  class="w-full px-3.5 py-2 bg-tika-bubbleBot border border-transparent rounded-lg text-body-chat focus:outline-none focus:border-tika-red focus:bg-white transition"
              />
            </div>

            <!-- Department (Dropdown Options - Sign Up only) -->
            <div v-if="!isLogin" class="space-y-1">
              <label class="text-[12px] font-semibold text-tika-dark">Departman</label>
              <select
                  v-model="form.department"
                  required
                  class="w-full px-3.5 py-2 bg-tika-bubbleBot border border-transparent rounded-lg text-body-chat focus:outline-none focus:border-tika-red focus:bg-white transition cursor-pointer text-tika-dark"
              >
                <option value="" disabled selected>Departmanınızı Seçin</option>
                <option value="external-relations">Dış İlişkiler ve Ortaklıklar Daire Başkanlığı</option>
                <option value="project-coordination">Projeler ve Koordinasyon Daire Başkanlığı</option>
                <option value="personnel-administrative">Personel ve İdari İşler Daire Başkanlığı</option>
                <option value="it">Bilgi Teknolojileri Daire Başkanlığı</option>
                <option value="strategy-development">Strateji Geliştirme Daire Başkanlığı</option>
                <option value="program-offices">Program Koordinasyon Ofisleri</option>
              </select>
            </div>

            <!-- Phone Number (Sign Up only) -->
            <div v-if="!isLogin" class="space-y-1">
              <label class="text-[12px] font-semibold text-tika-dark">Telefon Numarası</label>
              <input
                  v-model="form.phone"
                  type="tel"
                  placeholder="+90 5XX XXX XX XX"
                  required
                  class="w-full px-3.5 py-2 bg-tika-bubbleBot border border-transparent rounded-lg text-body-chat focus:outline-none focus:border-tika-red focus:bg-white transition"
              />
            </div>

            <!-- Submit Button -->
            <button
                type="submit"
                class="w-full mt-3 py-2.5 bg-tika-red hover:bg-tika-redHover text-white text-[14px] font-semibold rounded-lg shadow-xs transition-colors duration-200 cursor-pointer"
            >
              {{ isLogin ? 'GİRİŞ YAP →' : 'Hesap Oluştur →' }}
            </button>
          </form>

          <!-- Forgot Password Form -->
          <form v-else @submit.prevent="handleForgotPassword" class="space-y-4">
            <div class="space-y-1">
              <label class="text-[12px] font-semibold text-tika-dark">Kurumsal E-posta</label>
              <input
                  v-model="forgotEmail"
                  type="email"
                  placeholder="örn. isim@tika.gov.tr"
                  required
                  class="w-full px-3.5 py-2 bg-tika-bubbleBot border border-transparent rounded-lg text-body-chat focus:outline-none focus:border-tika-red focus:bg-white transition"
              />
            </div>

            <button
                type="submit"
                class="w-full py-2.5 bg-tika-red hover:bg-tika-redHover text-white text-[14px] font-semibold rounded-lg shadow-xs transition-colors duration-200 cursor-pointer"
            >
              Sıfırlama Talebi Gönder →
            </button>

            <div class="text-center">
              <button
                  type="button"
                  @click="isForgotPassword = false"
                  class="text-xs text-tika-textMuted hover:text-tika-dark underline cursor-pointer"
              >
                Girişe Dön
              </button>
            </div>
          </form>

          <!-- Bottom Toggle Button only visible if there's an Invite Token -->
          <p v-if="hasInviteToken && !isForgotPassword" class="mt-6 text-center text-footer-muted text-xs">
            {{ isLogin ? "Hesabınız yok mu?" : "Zaten bir hesabınız var mı?" }}
            <button
                @click="isLogin = !isLogin"
                class="text-tika-red font-semibold hover:underline ml-1 cursor-pointer"
            >
              {{ isLogin ? 'Kayıt Ol' : 'Giriş Yap' }}
            </button>
          </p>
        </template>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import TikaLogo from '../components/TikaLogo.vue'
import { useToast } from '../composables/useToast'

const route = useRoute()
const router = useRouter()
const { showToast } = useToast()

const isLogin = ref(true)
const hasInviteToken = ref(false)
const signupSuccess = ref(false)
const isForgotPassword = ref(false)
const forgotEmail = ref('')

const form = reactive({
  name: '',
  email: '',
  password: '',
  department: '',
  phone: ''
})

onMounted(() => {
  // Invite link is read from the URL (E.g: /login?token=abc123xyz)
  if (route.query.token || route.query.invite || route.path.includes('register')) {
    hasInviteToken.value = true
    isLogin.value = false
    if (route.query.email) {
      form.email = route.query.email
    }
  }
})

const goToLogin = () => {
  signupSuccess.value = false
  isLogin.value = true
  form.password = ''
}

const handleAuth = async () => {
  if (isLogin.value) {
    // -------------------------
    // 1. LOGIN ENDPOINT
    // -------------------------
    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: form.email,
          password: form.password
        })
      })

      if (response.ok) {
        const data = await response.json()

        // Save the Token and Role info coming from Java to the browser
        localStorage.setItem('token', data.token)
        localStorage.setItem('role', data.role) // will return 'Admin' or 'User'
        localStorage.setItem('userEmail', form.email)

        // Redirect to page based on role
        const role = data.role ? data.role.toLowerCase() : ''

        if (role === 'admin') {
          router.push('/admin')
        } else {
          // --- CHECK FOR SHARED CHAT (REDIRECT) ---
          const redirectUrl = localStorage.getItem('redirectUrl')

          if (redirectUrl) {
            localStorage.removeItem('redirectUrl') // Clear memory after retrieval
            router.push(redirectUrl) // Take the user directly to the shared chat!
          } else {
            router.push('/chat') // Normal login (no shared chat pending)
          }
          // --------------------------------------------------------
        }
      } else {
        const data = await response.json().catch(() => null)
        showToast(data?.message || 'Giriş başarısız. Lütfen e-posta ve şifrenizi kontrol edin.', 'error')
      }
    } catch (error) {
      console.error('Login Error:', error)
      showToast('Sunucuya bağlanılamadı. Docker servislerinin açık olduğundan emin olun.', 'error')
    }
  } else {
    // -------------------------
    // 2. ACCEPT INVITE (SIGN UP) ENDPOINT
    // -------------------------
    try {
      const response = await fetch('http://localhost:8080/api/auth/accept-invite', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          token: route.query.token || '', // Invite code obtained from the URL
          fullName: form.name,
          password: form.password,
          phoneNumber: form.phone,
          department: form.department
        })
      })

      if (response.ok) {
        signupSuccess.value = true
      } else {
        const data = await response.json().catch(() => null)
        showToast(data?.message || 'Kayıt başarısız. Davet linkinizin süresi dolmuş veya geçersiz olabilir.', 'error')
      }
    } catch (error) {
      console.error('Registration Error:', error)
      showToast('Sunucuya bağlanılamadı. Docker servislerinin açık olduğundan emin olun.', 'error')
    }
  }
}

// -------------------------
// 3. FORGOT PASSWORD REQUEST ENDPOINT
// -------------------------
const handleForgotPassword = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/auth/forgot-password', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: forgotEmail.value })
    })

    if (response.ok) {
      showToast('Şifre sıfırlama talebiniz yöneticilere iletildi.', 'success')
      isForgotPassword.value = false
      forgotEmail.value = ''
    } else {
      const data = await response.json().catch(() => null)
      showToast(data?.message || 'Talep oluşturulamadı. Lütfen e-posta adresinizi kontrol edin.', 'error')
    }
  } catch (error) {
    console.error('Password Reset Error:', error)
    showToast('Sunucuya bağlanılamadı.', 'error')
  }
}
</script>