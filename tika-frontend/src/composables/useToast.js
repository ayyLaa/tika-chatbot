import { ref } from 'vue'

const toasts = ref([])
let idCounter = 0

export function useToast() {
    const showToast = (message, type = 'error', duration = 4000) => {
        const id = idCounter++
        toasts.value.push({ id, message, type })
        setTimeout(() => {
            toasts.value = toasts.value.filter(t => t.id !== id)
        }, duration)
    }

    const dismissToast = (id) => {
        toasts.value = toasts.value.filter(t => t.id !== id)
    }

    return { toasts, showToast, dismissToast }
}