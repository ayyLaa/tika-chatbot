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
        
        <!-- Sekme Geçişi (Login / Sign Up) -->
        <div class="flex gap-2 p-1 bg-tika-bubbleBot rounded-lg mb-6">
          <button 
            @click="isLogin = true"
            :class="[
              'flex-1 py-2 text-[14px] font-semibold rounded-md transition-all duration-200',
              isLogin ? 'bg-tika-red text-white shadow-xs' : 'text-tika-textMuted hover:text-tika-dark'
            ]"
          >
            Login
          </button>
          <button 
            @click="isLogin = false"
            :class="[
              'flex-1 py-2 text-[14px] font-semibold rounded-md transition-all duration-200',
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
            {{ isLogin ? 'Get full access to the TİKA internal AI assistant.' : 'Get full access to the TİKA internal AI assistant.' }}
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
              type="email" 
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
            class="w-full mt-3 py-2.5 bg-tika-red hover:bg-tika-redHover text-white text-[14px] font-semibold rounded-lg shadow-xs transition-colors duration-200"
          >
            {{ isLogin ? 'LOG IN →' : 'Create Account →' }}
          </button>
        </form>

        <p class="mt-6 text-center text-footer-muted text-xs">
          {{ isLogin ? "Don't have an account?" : "Already have an account?" }}
          <button 
            @click="isLogin = !isLogin" 
            class="text-tika-red font-semibold hover:underline ml-1"
          >
            {{ isLogin ? 'Sign Up' : 'Log In' }}
          </button>
        </p>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import TikaLogo from '../components/TikaLogo.vue'

const router = useRouter()
const isLogin = ref(true)

const form = reactive({
  name: '',
  email: '',
  password: '',
  department: '',
  phone: ''
})

const handleAuth = () => {
  router.push('/')
}
</script>