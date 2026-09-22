<template>
  <div class="h-screen w-full flex bg-[#F4F5F7] font-sans items-center justify-center p-6">
    <div class="w-full max-w-md bg-white p-8 rounded-2xl shadow-xs border border-slate-100">
      <h2 class="text-xl font-bold mb-1 text-tika-dark">Yeni Şifre Belirleyin</h2>
      <p class="text-footer-muted text-[13px] mb-6">Lütfen aşağıya yeni şifrenizi girin.</p>

      <form @submit.prevent="handleResetPassword" class="space-y-4">
        <div class="space-y-1">
          <label class="text-[12px] font-semibold text-tika-dark">Yeni Şifre</label>
          <input
              v-model="newPassword"
              type="password"
              placeholder="••••••••"
              required
              class="w-full px-3.5 py-2 bg-tika-bubbleBot border border-transparent rounded-lg focus:outline-none focus:border-tika-red focus:bg-white transition"
          />
        </div>

        <button
            type="submit"
            class="w-full py-2.5 bg-tika-red hover:bg-tika-redHover text-white text-[14px] font-semibold rounded-lg shadow-xs transition cursor-pointer"
        >
          Şifreyi Güncelle →
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const newPassword = ref('')

const handleResetPassword = async () => {
  const token = route.query.token
  if (!token) {
    alert('Geçersiz veya eksik token.')
    return
  }

  try {
    const response = await fetch('http://localhost:8080/api/auth/reset-password', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        token: token,
        newPassword: newPassword.value
      })
    })

    if (response.ok) {
      alert('Şifreniz başarıyla güncellendi! Şimdi giriş yapabilirsiniz.')
      router.push('/login')
    } else {
      alert('Şifre sıfırlama başarısız. Token süresi dolmuş olabilir.')
    }
  } catch (error) {
    console.error('Password Update Error:', error)
    alert('Sunucuya bağlanılamadı.')
  }
}
</script>