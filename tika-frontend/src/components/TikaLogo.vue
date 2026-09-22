<template>
  <div class="flex items-center gap-1 select-none">
    <!-- Full Crescent and Star SVG with Cropped Right Margin -->
    <svg class="w-12 h-12 text-white shrink-0" viewBox="150 150 720 500" fill="currentColor">
      <!-- Crescent -->
      <path fill-rule="evenodd" clip-rule="evenodd" d="M 400 150 C 538 150 650 262 650 400 C 650 538 538 650 400 650 C 262 650 150 538 150 400 C 150 262 262 150 400 150 Z M 450 200 C 560 200 650 290 650 400 C 650 510 560 600 450 600 C 340 600 250 510 250 400 C 250 290 340 200 450 200 Z" />
      <!-- Star -->
      <polygon points="735,320 758,390 832,390 772,433 795,503 735,460 675,503 698,433 638,390 712,390"
        style="transform: scale(1.3); transform-origin: 735px 411px;" />
    </svg>

    <!-- TIK + AI Capsule Text (Proportional Spacing) -->
    <div class="flex items-center gap-1.5 font-black tracking-tight text-white text-2xl lg:text-3xl ml-0.5">
      <span>TIK</span>
      <span class="bg-white text-tika-red px-3 py-1 rounded-full text-2xl font-extrabold uppercase shadow-sm flex items-center justify-center leading-none">
        AI
      </span>
    </div>
  </div>
</template>