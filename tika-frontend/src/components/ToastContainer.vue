<template>
    <teleport to="body">
        <div class="fixed top-5 right-5 z-[9999] space-y-2 w-full max-w-sm">
            <transition-group name="toast">
                <div v-for="toast in toasts" :key="toast.id"
                :class="[
                'rounded-xl shadow-lg border p-4 flex items-start gap-3 text-sm',
                toast.type === 'success' ? 'bg-emerald-50 border-emerald-200 text-emerald-800' : 'bg-red-50 border-red-200 text-red-800'
                ]">
                <span class="text-lg leading-none mt-0.5">{{ toast.type === 'success' ? '✓' : '⚠️' }}</span>
                <p class="flex-1 leading-snug">{{ toast.message }}</p>
                <button @click="dismissToast(toast.id)" class="text-current opacity-50 hover:opacity-100 cursor-pointer leading-none text-base">✕</button>
        </div>
    </transition-group>
</div>
</teleport>
</template>

<script setup>
    import { useToast } from '../composables/useToast'
    const { toasts, dismissToast } = useToast()
</script>

<style scoped>
    .toast-enter-active, .toast-leave-active { transition: all 0.25s ease; }
    .toast-enter-from { opacity: 0; transform: translateX(30px); }
    .toast-leave-to { opacity: 0; transform: translateX(30px); }
</style>