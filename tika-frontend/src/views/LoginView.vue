<template>
  <div class="h-screen w-full flex bg-[#F4F5F7] font-sans overflow-hidden">
    <!-- Sol Taraf: Canlı Kırmızı Marka Alanı -->
    <div class="hidden lg:flex lg:w-2/5 bg-tika-red p-10 flex-col justify-between relative overflow-hidden h-full">
      <!-- Ay-Yıldızlı TİKAİ Logo -->
      <TikaLogo />

      <!-- Slogan Alanı -->
      <div class="my-auto max-w-lg z-10 space-y-0 mt-26">
        <img
            src="../assets/slogan.png"
            alt="From the Heart of Turkish People"
            class="w-full max-w-[400px] h-auto object-contain select-none"
        />

        <!-- Açıklama Metni -->
        <p class="text-white/90 text-[13px] leading-[1.6] font-normal relative -top-14">
          This AI assistant is tailored for TİKA staff to search internal data programs, procedures and reporting, one question at a time.
        </p>
      </div>

      <!-- Alt Bilgi / Footer -->
      <div class="text-[10px] text-[#EBF1F8]">
        © 2026 TİKA — Turkish Cooperation and Coordination Agency
      </div>
    </div>

    <!-- Sağ Taraf: Form Alanı -->
    <div class="w-full lg:w-3/5 flex items-center justify-center p-6 sm:p-10 h-full overflow-y-auto lg:overflow-y-hidden">
      <!-- Tam ekrana orantılı geniş kart -->
      <div class="w-full max-w-3xl bg-white p-8 sm:p-10 rounded-2xl shadow-xs border border-slate-100">

        <!-- Sekme Geçişi (Sadece Davet Token'ı Varsa Sign Up Görünür) -->
        <div v-if="hasInviteToken" class="flex gap-2 p-1 bg-tika-bubbleBot rounded-lg mb-6">
          <button
              @click="isLogin = true"
              :class="[
              'flex-1 py-2 text-[14px] font-semibold rounded-md transition-all duration-200 cursor-pointer',
              isLogin ? 'bg-tika-red text-white shadow-xs' : 'text-tika-textMuted hover:text-tika-dark'
            ]"
          >
            Login
          </button>
          <button
              @click="isLogin = false"
              :class="[
              'flex-1 py-2 text-[14px] font-semibold rounded-md transition-all duration-200 cursor-pointer',
              !isLogin ? 'bg-tika-red text-white shadow-xs' : 'text-tika-textMuted hover:text-tika-dark'
            ]"
          >
            Sign Up
          </button>
        </div>

        <!-- Başlıklar -->
        <div class="mb-5">
          <h2 class="text-xl font-bold mb-1 text-tika-dark">
            {{ isLogin ? 'Welcome Back' : 'Create Your Account' }}
          </h2>
          <p class="text-footer-muted text-[13px]">
            {{ isLogin ? 'Get full access to the TİKA internal AI assistant.' : 'Complete your registration via corporate invitation.' }}
          </p>
        </div>

        <!-- Form -->
        <form @submit.prevent="handleAuth" class="space-y-3">
          <!-- Full Name (Sadece Sign Up) -->
          <div v-if="!isLogin" class="space-y-1">
            <label class="text-[12px] font-semibold text-tika-dark">Full Name</label>
            <input
                v-model="form.name"
                type="text"
                placeholder="E.g. Elif Yılmaz"
                required
                class="w-full px-3.5 py-2 bg-tika-bubbleBot border border-transparent rounded-lg text-body-chat focus:outline-none focus:border-tika-red focus:bg-white transition"
            />
          </div>

          <!-- Email -->
          <div class="space-y-1">
            <label class="text-[12px] font-semibold text-tika-dark">Email</label>
            <input
                v-model="form.email"
                type="text"
                placeholder="e.g. name@tika.gov.tr"
                required
                class="w-full px-3.5 py-2 bg-tika-bubbleBot border border-transparent rounded-lg text-body-chat focus:outline-none focus:border-tika-red focus:bg-white transition"
            />
          </div>

          <!-- Password -->
          <div class="space-y-1">
            <label class="text-[12px] font-semibold text-tika-dark">Password</label>
            <input
                v-model="form.password"
                type="password"
                placeholder="••••••••"
                required
                class="w-full px-3.5 py-2 bg-tika-bubbleBot border border-transparent rounded-lg text-body-chat focus:outline-none focus:border-tika-red focus:bg-white transition"
            />
          </div>

          <!-- Department (Açılır Seçenekler - Sadece Sign Up) -->
          <div v-if="!isLogin" class="space-y-1">
            <label class="text-[12px] font-semibold text-tika-dark">Department</label>
            <select
                v-model="form.department"
                required
                class="w-full px-3.5 py-2 bg-tika-bubbleBot border border-transparent rounded-lg text-body-chat focus:outline-none focus:border-tika-red focus:bg-white transition cursor-pointer text-tika-dark"
            >
              <option value="" disabled selected>Select Your Department</option>
              <option value="external-relations">Department of External Relations and Partnerships</option>
              <option value="project-coordination">Department of Projects and Coordination</option>
              <option value="personnel-administrative">Department of Personnel and Administrative Affairs</option>
              <option value="it">Department of Information Technology</option>
              <option value="strategy-development">Department of Strategy Development</option>
              <option value="program-offices">Program Coordination Offices</option>
            </select>
          </div>

          <!-- Phone Number (Sadece Sign Up) -->
          <div v-if="!isLogin" class="space-y-1">
            <label class="text-[12px] font-semibold text-tika-dark">Phone Number</label>
            <input
                v-model="form.phone"
                type="tel"
                placeholder="+90 5XX XXX XX XX"
                required
                class="w-full px-3.5 py-2 bg-tika-bubbleBot border border-transparent rounded-lg text-body-chat focus:outline-none focus:border-tika-red focus:bg-white transition"
            />
          </div>

          <!-- Submit Butonu -->
          <button
              type="submit"
              class="w-full mt-3 py-2.5 bg-tika-red hover:bg-tika-redHover text-white text-[14px] font-semibold rounded-lg shadow-xs transition-colors duration-200 cursor-pointer"
          >
            {{ isLogin ? 'LOG IN →' : 'Create Account →' }}
          </button>
        </form>

        <!-- Sadece Davet Token'ı Varsa Alt Geçiş Butonu Görünür -->
        <p v-if="hasInviteToken" class="mt-6 text-center text-footer-muted text-xs">
          {{ isLogin ? "Don't have an account?" : "Already have an account?" }}
          <button
              @click="isLogin = !isLogin"
              class="text-tika-red font-semibold hover:underline ml-1 cursor-pointer"
          >
            {{ isLogin ? 'Sign Up' : 'Log In' }}
          </button>
        </p>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import TikaLogo from '../components/TikaLogo.vue'

const route = useRoute()
const router = useRouter()

const isLogin = ref(true)
const hasInviteToken = ref(false)

const form = reactive({
  name: '',
  email: '',
  password: '',
  department: '',
  phone: ''
})

onMounted(() => {
  // Davet linki URL'den okunuyor (Örn: /login?token=abc123xyz)
  if (route.query.token || route.query.invite || route.path.includes('register')) {
    hasInviteToken.value = true
    isLogin.value = false
    if (route.query.email) {
      form.email = route.query.email
    }
  }
})

const handleAuth = async () => {
  if (isLogin.value) {
    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: form.email, password: form.password })
      })

      if (response.ok) {
        const data = await response.json()
        localStorage.setItem('token', data.token)
        localStorage.setItem('role', data.role)
        localStorage.setItem('userEmail', form.email)

        const role = data.role ? data.role.toLowerCase() : ''
        if (role === 'admin') {
          router.push('/admin')
        } else {
          const redirectUrl = localStorage.getItem('redirectUrl')
          if (redirectUrl) {
            localStorage.removeItem('redirectUrl')
            router.push(redirectUrl)
          } else {
            router.push('/chat')
          }
        }
      } else {
        const data = await response.json().catch(() => null)
        alert(data?.message || 'Giriş başarısız. Lütfen e-posta ve şifrenizi kontrol edin.')
      }
    } catch (error) {
      console.error('Login Hatası:', error)
      alert('Sunucuya bağlanılamadı. Docker servislerinin açık olduğundan emin olun.')
    }
  } else {
    try {
      const response = await fetch('http://localhost:8080/api/auth/accept-invite', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          token: route.query.token || '',
          fullName: form.name,
          password: form.password,
          phoneNumber: form.phone,
          department: form.department
        })
      })

      if (response.ok) {
        alert('Hesabınız başarıyla oluşturuldu! Şimdi giriş yapabilirsiniz.')
        isLogin.value = true
        form.password = ''
      } else {
        const data = await response.json().catch(() => null)
        alert(data?.message || 'Kayıt başarısız. Davet linkinizin süresi dolmuş veya geçersiz olabilir.')
      }
    } catch (error) {
      console.error('Kayıt Hatası:', error)
      alert('Sunucuya bağlanılamadı. Docker servislerinin açık olduğundan emin olun.')
    }
  }
}
</script>