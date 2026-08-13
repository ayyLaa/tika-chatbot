<template>
  <div class="min-h-screen bg-[#F3F4F6] flex flex-col text-[#1F2937] relative">
    <!-- Üst Bar (Header - Koyu Lacivert: #031B39) -->
    <header class="h-16 bg-[#031B39] px-6 flex items-center justify-between z-20 shrink-0 shadow-md relative">
      <!-- Sol: TİKA Logo -->
      <div class="flex items-center">
        <img 
          src="../assets/logo.png" 
          alt="TİKA AI Logo" 
          class="h-16 w-auto object-contain select-none cursor-pointer"
        />
      </div>

      <!-- Sağ: Kullanıcı Bilgisi & Profil Menüsü -->
      <div class="relative">
        <button 
          @click="showProfileMenu = !showProfileMenu"
          class="flex items-center space-x-3 focus:outline-none cursor-pointer p-1 rounded hover:bg-slate-800/60 transition"
        >
          <div class="w-8 h-8 bg-slate-700 rounded flex items-center justify-center font-bold text-sm text-white border border-slate-500">
            EY
          </div>
          <div class="text-left text-xs">
            <div class="font-bold text-white leading-tight">Elif Yılmaz</div>
            <div class="text-slate-400 font-semibold text-[10px] tracking-wider">ADMIN</div>
          </div>
        </button>

        <!-- Profil Dropdown Menüsü -->
        <div 
          v-if="showProfileMenu" 
          class="absolute right-0 mt-2 w-72 bg-white rounded-xl shadow-2xl border border-gray-100 z-50 overflow-hidden text-xs"
        >
          <div class="p-4 space-y-2">
            <div class="font-bold text-gray-900 text-sm">Elif Yılmaz</div>
            <div class="text-gray-500 text-xs">elif.yilmaz@tika.gov.tr</div>
            <div class="inline-block bg-red-50 text-[#E30613] font-medium px-2.5 py-1 rounded-md text-[11px]">
              Department of Information Technology
            </div>
          </div>
          <div class="border-t border-gray-100 p-2">
            <button 
              @click="handleLogout"
              class="w-full text-left px-3 py-2 text-red-600 font-semibold hover:bg-red-50 rounded-lg flex items-center space-x-2 transition cursor-pointer"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 stroke-current" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
              </svg>
              <span>Sign Out</span>
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- Ana Gövde -->
    <div class="flex flex-1">
      <!-- Sol Menü (Sidebar) -->
      <aside class="w-64 bg-[#EAEAEA] border-r border-gray-300 flex flex-col justify-between p-4">
        <div>
          <h2 class="text-xs font-bold text-gray-500 uppercase tracking-wider mb-4 px-2">YÖNETİM KONSOLU</h2>
          <nav class="space-y-1">
            <button 
              @click="activeTab = 'users'" 
              :class="activeTab === 'users' ? 'bg-[#1F2937] text-white' : 'text-gray-700 hover:bg-gray-300'"
              class="w-full text-left px-3 py-2 rounded text-sm font-semibold flex justify-between items-center transition cursor-pointer"
            >
              <span>Kullanıcılar</span>
              <span class="text-xs opacity-75">248</span>
            </button>

            <button 
              @click="activeTab = 'vector'" 
              :class="activeTab === 'vector' ? 'bg-[#1F2937] text-white' : 'text-gray-700 hover:bg-gray-300'"
              class="w-full text-left px-3 py-2 rounded text-sm font-semibold flex justify-between items-center transition cursor-pointer"
            >
              <span>Vektör Veritabanı</span>
              <span class="text-xs opacity-75">1.325</span>
            </button>

            <button 
              @click="activeTab = 'analytics'" 
              :class="activeTab === 'analytics' ? 'bg-[#1F2937] text-white' : 'text-gray-700 hover:bg-gray-300'"
              class="w-full text-left px-3 py-2 rounded text-sm font-semibold flex justify-between items-center transition cursor-pointer"
            >
              <span>Analitik & Loglar</span>
              <span class="text-xs opacity-75">8,4M</span>
            </button>
          </nav>
        </div>

        <div class="text-xs text-gray-500 space-y-1 border-t border-gray-300 pt-3">
          <div class="font-bold uppercase text-[10px] tracking-wider text-gray-400">SİSTEM</div>
          <div class="flex items-center space-x-2 text-gray-700 font-medium">
            <span class="w-2 h-2 bg-red-600 rounded-full animate-pulse"></span>
            <span>Vektör servisi çalışıyor</span>
          </div>
          <div class="text-[11px] text-gray-400">Son senkr. 12.08.2026 09:40</div>
        </div>
      </aside>

      <!-- Sağ İçerik Alanı -->
      <main class="flex-1 p-6">
        <!-- KULLANICILAR TAB'İ -->
        <div v-if="activeTab === 'users'" class="space-y-6">
          <div class="flex justify-between items-center">
            <div>
              <span class="text-[11px] font-bold text-red-600 tracking-wider uppercase">ERİŞİM VE ROLLER</span>
              <h1 class="text-3xl font-extrabold text-black">Kullanıcı Yönetimi</h1>
            </div>
            <div>
              <button 
                @click="showAddUserModal = true"
                class="px-4 py-2 bg-[#E30613] text-white rounded text-sm font-semibold hover:bg-red-700 transition cursor-pointer"
              >
                + Yeni kullanıcı
              </button>
            </div>
          </div>

          <div class="grid grid-cols-4 gap-4">
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">TOPLAM PERSONEL</div>
              <div class="text-3xl font-black text-black mt-1">248</div>
            </div>
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">ADMİN</div>
              <div class="text-3xl font-black text-black mt-1">6</div>
            </div>
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">ASKIDA</div>
              <div class="text-3xl font-black text-black mt-1">1</div>
            </div>
            <div class="bg-[#E30613] text-white p-4 rounded shadow-sm">
              <div class="text-xs font-bold tracking-wider opacity-90">BEKLEYEN ŞİFRE TALEBİ</div>
              <div class="text-3xl font-black mt-1">2</div>
            </div>
          </div>

          <div class="grid grid-cols-3 gap-6">
            <div class="col-span-2 space-y-4">
              <div class="flex justify-between items-center gap-4">
                <input 
                  v-model="searchQuery"
                  type="text" 
                  placeholder="Ad veya e-posta ara" 
                  class="flex-1 bg-gray-200 text-sm px-4 py-2 rounded border border-gray-300 focus:outline-none focus:border-gray-500"
                />
                <div class="flex border border-gray-300 rounded overflow-hidden text-xs font-semibold">
                  <button @click="roleFilter = 'all'" :class="roleFilter === 'all' ? 'bg-[#1F2937] text-white' : 'bg-gray-100 text-gray-700'" class="px-3 py-2 cursor-pointer">Tümü</button>
                  <button @click="roleFilter = 'admin'" :class="roleFilter === 'admin' ? 'bg-[#1F2937] text-white' : 'bg-gray-100 text-gray-700'" class="px-3 py-2 border-l border-r border-gray-300 cursor-pointer">Admin</button>
                  <button @click="roleFilter = 'user'" :class="roleFilter === 'user' ? 'bg-[#1F2937] text-white' : 'bg-gray-100 text-gray-700'" class="px-3 py-2 cursor-pointer">User</button>
                </div>
              </div>

              <div class="bg-white border border-gray-200 rounded shadow-sm overflow-hidden">
                <table class="w-full text-left text-xs">
                  <thead class="bg-gray-100 border-b border-gray-200 text-gray-500 font-bold uppercase tracking-wider">
                    <tr>
                      <th class="p-3">PERSONEL</th>
                      <th class="p-3">ROL</th>
                      <th class="p-3">DURUM</th>
                      <th class="p-3">SON GİRİŞ</th>
                      <th class="p-3 text-right">İŞLEM</th>
                    </tr>
                  </thead>
                  <tbody class="divide-y divide-gray-200 font-medium text-gray-800">
                    <tr v-for="user in filteredUsers" :key="user.email" class="hover:bg-gray-50">
                      <td class="p-3 flex items-center space-x-3">
                        <div class="w-7 h-7 bg-black text-white rounded font-bold text-[10px] flex items-center justify-center">
                          {{ user.initials }}
                        </div>
                        <div>
                          <div class="font-bold text-gray-900">{{ user.name }}</div>
                          <div class="text-[11px] text-gray-400">{{ user.email }}</div>
                        </div>
                      </td>
                      <td class="p-3">
                        <span 
                          :class="user.role === 'Admin' ? 'bg-[#E30613] text-white' : 'bg-gray-200 text-gray-700'"
                          class="px-2 py-0.5 rounded font-bold text-[10px]"
                        >
                          {{ user.role }}
                        </span>
                      </td>
                      <td class="p-3 text-[11px]">
                        <span class="flex items-center space-x-1.5">
                          <span 
                            class="w-1.5 h-1.5 rounded-full"
                            :class="{
                              'bg-black': user.status === 'Aktif',
                              'bg-red-500': user.status === 'Askıda',
                              'bg-gray-400': user.status === 'Davet bekliyor'
                            }"
                          ></span>
                          <span :class="{'text-red-600 font-semibold': user.status === 'Askıda', 'text-gray-400': user.status === 'Davet bekliyor'}">{{ user.status }}</span>
                        </span>
                      </td>
                      <td class="p-3 text-gray-500 text-[11px]">{{ user.lastLogin }}</td>
                      <td class="p-3 text-right space-x-1">
                        <button v-if="user.status !== 'Askıda'" class="px-2 py-1 border border-gray-300 rounded text-[10px] font-semibold text-gray-700 hover:bg-gray-100 cursor-pointer">Askıya al</button>
                        <button v-else class="px-2 py-1 border border-gray-300 rounded text-[10px] font-semibold text-gray-700 hover:bg-gray-100 cursor-pointer">Geri al</button>
                        <button class="px-2 py-1 border border-gray-300 rounded text-[10px] font-semibold text-red-600 hover:bg-red-50 cursor-pointer">Sil</button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <div class="space-y-6">
              <div class="bg-gray-100 p-4 rounded border border-gray-200 space-y-4">
                <div>
                  <span class="text-[10px] font-bold text-red-600 tracking-wider uppercase">Rol ve Yetkilendirme</span>
                  <h3 class="font-bold text-gray-900 text-sm">Şifre sıfırlama talepleri</h3>
                </div>

                <div class="space-y-3">
                  <div class="bg-white p-3 rounded border border-gray-200 space-y-2">
                    <div>
                      <div class="font-bold text-xs">Burak Demirtaş</div>
                      <div class="text-[10px] text-gray-400">Talep 12.08.2026 08:10 · 2. deneme</div>
                    </div>
                    <div class="flex space-x-2">
                      <button class="px-3 py-1 bg-[#E30613] text-white rounded text-[10px] font-bold hover:bg-red-700 cursor-pointer">Onayla</button>
                      <button class="px-3 py-1 border border-gray-300 rounded text-[10px] font-bold text-gray-700 hover:bg-gray-100 cursor-pointer">Reddet</button>
                    </div>
                  </div>

                  <div class="bg-white p-3 rounded border border-gray-200 space-y-2">
                    <div>
                      <div class="font-bold text-xs">Onur Şahin</div>
                      <div class="text-[10px] text-gray-400">Talep 11.08.2026 16:22 · SSO dışı giriş</div>
                    </div>
                    <div class="flex space-x-2">
                      <button class="px-3 py-1 bg-[#E30613] text-white rounded text-[10px] font-bold hover:bg-red-700 cursor-pointer">Onayla</button>
                      <button class="px-3 py-1 border border-gray-300 rounded text-[10px] font-bold text-gray-700 hover:bg-gray-100 cursor-pointer">Reddet</button>
                    </div>
                  </div>
                </div>
              </div>

              <div class="bg-gray-100 p-4 rounded border border-gray-200 space-y-3">
                <span class="text-[10px] font-bold text-gray-400 tracking-wider uppercase">BİLGİ HAVUZU ERİŞİMİ</span>
                
                <div class="space-y-2 text-xs font-semibold">
                  <div class="flex justify-between items-center">
                    <span>İnsan Kaynakları</span>
                    <div class="flex items-center space-x-2">
                      <span class="text-[10px] text-gray-400">Tüm personel</span>
                      <input type="checkbox" checked class="accent-red-600 w-4 h-4 cursor-pointer" />
                    </div>
                  </div>
                  <div class="flex justify-between items-center">
                    <span>Mali İşler</span>
                    <div class="flex items-center space-x-2">
                      <span class="text-[10px] text-gray-400">Admin + Mali</span>
                      <input type="checkbox" checked class="accent-red-600 w-4 h-4 cursor-pointer" />
                    </div>
                  </div>
                  <div class="flex justify-between items-center">
                    <span>Bilgi Teknolojileri</span>
                    <div class="flex items-center space-x-2">
                      <span class="text-[10px] text-gray-400">Admin</span>
                      <input type="checkbox" class="accent-red-600 w-4 h-4 cursor-pointer" />
                    </div>
                  </div>
                  <div class="flex justify-between items-center">
                    <span>Genel Arşiv</span>
                    <div class="flex items-center space-x-2">
                      <span class="text-[10px] text-gray-400">Tüm personel</span>
                      <input type="checkbox" checked class="accent-red-600 w-4 h-4 cursor-pointer" />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- VEKTÖR VERİTABANI TAB'İ -->
        <div v-else-if="activeTab === 'vector'" class="space-y-6">
          <div class="flex justify-between items-center">
            <div>
              <span class="text-[11px] font-bold text-red-600 tracking-wider uppercase">BİLGİ HAVUZU DURUMU</span>
              <h1 class="text-3xl font-extrabold text-black">Vektör Veritabanı</h1>
            </div>
          </div>

          <div class="grid grid-cols-4 gap-4">
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">INDEXED</div>
              <div class="text-3xl font-black text-black mt-1">1.284</div>
            </div>
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">PENDING</div>
              <div class="text-3xl font-black text-black mt-1">37</div>
            </div>
            <div class="bg-[#E30613] text-white p-4 rounded shadow-sm">
              <div class="text-xs font-bold tracking-wider opacity-90">FAILED</div>
              <div class="text-3xl font-black mt-1">4</div>
            </div>
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">TOPLAM CHUNK</div>
              <div class="text-3xl font-black text-black mt-1">412.905</div>
            </div>
          </div>

          <div class="flex justify-between items-center text-xs text-gray-500 bg-gray-200 p-2.5 rounded border border-gray-300">
            <div>
              Son tam senkronizasyon: <strong class="text-gray-700">12.08.2026 09:40</strong> · Embedding modeli: <span class="font-mono text-gray-700">text-embedding-3-large</span> · Chunk <span class="font-mono text-gray-700">800/120</span>
            </div>
            <button class="px-3 py-1 bg-white border border-gray-300 rounded font-semibold text-gray-700 hover:bg-gray-100 cursor-pointer">Yeniden indeksle</button>
          </div>

          <div class="bg-white border border-gray-200 rounded shadow-sm overflow-hidden">
            <table class="w-full text-left text-xs">
              <thead class="bg-gray-100 border-b border-gray-200 text-gray-500 font-bold uppercase tracking-wider">
                <tr>
                  <th class="p-3">DOKÜMAN</th>
                  <th class="p-3">HAVUZ</th>
                  <th class="p-3">DURUM</th>
                  <th class="p-3">CHUNK</th>
                  <th class="p-3">SON SENKR.</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-200 font-medium text-gray-800">
                <tr v-for="(doc, i) in documents" :key="i" class="hover:bg-gray-50">
                  <td class="p-3 font-semibold">{{ doc.name }}</td>
                  <td class="p-3 text-gray-500">{{ doc.pool }}</td>
                  <td class="p-3">
                    <span 
                      :class="{
                        'text-gray-700 font-bold': doc.status === 'INDEXED',
                        'bg-gray-200 px-2 py-0.5 rounded text-gray-600': doc.status === 'PENDING',
                        'bg-[#E30613] text-white px-2 py-0.5 rounded font-bold': doc.status === 'FAILED'
                      }"
                    >
                      {{ doc.status }}
                    </span>
                  </td>
                  <td class="p-3 font-mono">{{ doc.chunk }}</td>
                  <td class="p-3 text-gray-500">{{ doc.lastSync }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- ANALİTİK & LOGLAR TAB'İ -->
        <div v-else-if="activeTab === 'analytics'" class="space-y-6">
          <div class="flex justify-between items-center">
            <div>
              <span class="text-[11px] font-bold text-red-600 tracking-wider uppercase">KULLANIM VE PERFORMANS</span>
              <h1 class="text-3xl font-extrabold text-black">Analitik ve Log Paneli</h1>
            </div>
          </div>

          <div class="grid grid-cols-4 gap-4">
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">AYLIK TOKEN</div>
              <div class="text-3xl font-black text-black mt-1">8,4M</div>
            </div>
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">ORT. YANIT SÜRESİ</div>
              <div class="text-3xl font-black text-black mt-1">1,9s</div>
            </div>
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">AKTİF KULLANICI</div>
              <div class="text-3xl font-black text-black mt-1">176</div>
            </div>
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">MEMNUNİYET</div>
              <div class="text-3xl font-black text-black mt-1">%87</div>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-6">
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm flex flex-col justify-between">
              <div class="text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-4">GÜNLÜK TOKEN TÜKETİMİ · SON 14 GÜN</div>
              
              <div class="h-40 flex items-end justify-between gap-2 px-2 border-b border-gray-200 pb-2">
                <div v-for="(bar, idx) in chartBars" :key="idx" class="w-full flex flex-col items-center group">
                  <div 
                    :style="{ height: bar.height + '%' }" 
                    :class="bar.highlight ? 'bg-[#E30613]' : 'bg-[#1F2937] hover:bg-gray-700'"
                    class="w-full rounded-t transition-all"
                  ></div>
                </div>
              </div>

              <div class="flex justify-between text-[10px] text-gray-400 font-semibold pt-2">
                <span>30 Tem</span>
                <span>6 Ağu</span>
                <span>12 Ağu</span>
              </div>
            </div>

            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm space-y-3">
              <div class="text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-2">EN ÇOK SORGULANAN KONULAR</div>
              
              <div v-for="(topic, idx) in topTopics" :key="idx" class="space-y-1">
                <div class="flex justify-between text-xs font-semibold text-gray-800">
                  <span>{{ topic.title }}</span>
                  <span class="text-gray-500 font-mono text-[11px]">{{ topic.count }}</span>
                </div>
                <div class="w-full bg-gray-100 rounded-full h-2 overflow-hidden">
                  <div class="bg-[#E30613] h-2 rounded-full" :style="{ width: topic.percentage + '%' }"></div>
                </div>
              </div>
            </div>
          </div>

          <!-- SORU-CEVAP GEÇMİŞİ & AI RİSK SÜZGECİ -->
          <div class="space-y-3">
            <div class="flex justify-between items-center">
              <span class="text-[10px] font-bold text-gray-400 tracking-wider uppercase">SORU-CEVAP GEÇMİŞİ & AI RİSK SÜZGECİ</span>
              
              <!-- Filtre Sekmeleri -->
              <div class="flex border border-gray-300 rounded overflow-hidden text-xs font-semibold">
                <button 
                  @click="feedbackFilter = 'flagged'" 
                  :class="feedbackFilter === 'flagged' ? 'bg-[#E30613] text-white font-bold' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'" 
                  class="px-3 py-1.5 flex items-center gap-1.5 cursor-pointer border-r border-gray-300"
                >
                  <span>⚠️ AI Riskli Buldukları</span>
                  <span class="bg-white text-[#E30613] px-1.5 py-0.2 rounded-full text-[10px] font-extrabold">{{ flaggedCount }}</span>
                </button>
                <button 
                  @click="feedbackFilter = 'all'" 
                  :class="feedbackFilter === 'all' ? 'bg-[#1F2937] text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'" 
                  class="px-3 py-1.5 cursor-pointer"
                >
                  Tümü
                </button>
                <button 
                  @click="feedbackFilter = 'positive'" 
                  :class="feedbackFilter === 'positive' ? 'bg-[#1F2937] text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'" 
                  class="px-3 py-1.5 border-l border-r border-gray-300 cursor-pointer"
                >
                  Olumlu
                </button>
                <button 
                  @click="feedbackFilter = 'negative'" 
                  :class="feedbackFilter === 'negative' ? 'bg-[#1F2937] text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'" 
                  class="px-3 py-1.5 cursor-pointer"
                >
                  Olumsuz
                </button>
              </div>
            </div>

            <!-- Soru Cevap Tablosu -->
            <div class="bg-white border border-gray-200 rounded shadow-sm overflow-hidden">
              <table class="w-full text-left text-xs">
                <thead class="bg-gray-100 border-b border-gray-200 text-gray-500 font-bold uppercase tracking-wider">
                  <tr>
                    <th class="p-3">ZAMAN</th>
                    <th class="p-3">KULLANICI</th>
                    <th class="p-3">SORU</th>
                    <th class="p-3">AI RİSK TESPİTİ & AÇIKLAMA</th>
                    <th class="p-3 text-right">GERİ BİLDİRİM & AKSİYON</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 font-medium text-gray-800">
                  <tr v-for="(log, idx) in filteredLogs" :key="idx" class="hover:bg-gray-50">
                    <td class="p-3 text-gray-500 font-mono text-[11px]">{{ log.time }}</td>
                    <td class="p-3 font-bold text-gray-900">{{ log.user }}</td>
                    <td class="p-3 text-gray-800 font-semibold max-w-xs">{{ log.question }}</td>
                    
                    <!-- AI Risk Tespiti & Gerekçe Sütunu -->
                    <td class="p-3">
                      <div v-if="log.isFlagged" class="bg-red-50 text-red-700 border border-red-200 p-2 rounded text-[11px] leading-snug">
                        <span class="font-bold block mb-0.5">⚠️ {{ log.riskLevel || 'Orta' }} Risk Tespiti:</span>
                        {{ log.aiRiskReason }}
                      </div>
                      <div v-else-if="log.reason" class="text-slate-600 text-[11px] italic">
                        <b>Açıklama:</b> {{ log.reason }}
                      </div>
                      <div v-else class="text-gray-400 italic text-[11px]">
                        Temiz / Risk Tespit Edilmedi
                      </div>
                    </td>

                    <!-- Aksiyon / Feedback Sütunu -->
                    <td class="p-3 text-right">
                      <div class="flex items-center justify-end gap-1.5">
                        <button 
                          @click="markAsSafe(log)" 
                          :class="log.feedback === 'Olumlu' ? 'bg-amber-100 text-amber-800 border-amber-300' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
                          class="px-2.5 py-1 rounded border border-gray-200 font-bold text-[10px] transition cursor-pointer"
                        >
                          👍 Olumlu
                        </button>
                        <button 
                          @click="openFeedbackModal(log)" 
                          :class="log.feedback === 'Olumsuz' ? 'bg-red-100 text-red-800 border-red-300' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
                          class="px-2.5 py-1 rounded border border-gray-200 font-bold text-[10px] transition cursor-pointer"
                        >
                          👎 Olumsuz
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- YENİ KULLANICI EKLE MODAL -->
    <div 
      v-if="showAddUserModal" 
      class="fixed inset-0 bg-black/40 backdrop-blur-xs flex items-center justify-center z-50 p-4"
    >
      <div class="bg-[#EAEAEA] rounded-lg shadow-2xl w-full max-w-md p-6 space-y-5 border border-gray-300">
        <h2 class="text-xl font-extrabold text-gray-900">Yeni kullanıcı ekle</h2>
        
        <div class="space-y-4 text-xs">
          <div>
            <label class="block font-semibold text-gray-600 mb-1">Kurumsal e-posta</label>
            <input 
              v-model="newUserEmail" 
              type="email" 
              placeholder="ad.soyad@kurum.gov.tr" 
              class="w-full bg-white border border-gray-300 rounded px-3 py-2 text-gray-800 focus:outline-none focus:border-gray-500 text-sm"
            />
          </div>

          <div>
            <label class="block font-semibold text-gray-600 mb-1">Rol</label>
            <div class="inline-flex border border-gray-300 rounded overflow-hidden">
              <button 
                @click="newUserRole = 'User'" 
                :class="newUserRole === 'User' ? 'bg-[#E30613] text-white font-bold' : 'bg-gray-200 text-gray-700'" 
                class="px-4 py-1.5 transition cursor-pointer"
              >
                User
              </button>
              <button 
                @click="newUserRole = 'Admin'" 
                :class="newUserRole === 'Admin' ? 'bg-[#E30613] text-white font-bold' : 'bg-gray-200 text-gray-700'" 
                class="px-4 py-1.5 border-l border-gray-300 transition cursor-pointer"
              >
                Admin
              </button>
            </div>
          </div>
        </div>

        <div class="flex justify-end space-x-3 pt-2">
          <button 
            @click="showAddUserModal = false" 
            class="px-4 py-2 border border-gray-300 rounded text-xs font-bold text-gray-700 hover:bg-gray-200 transition cursor-pointer"
          >
            Vazgeç
          </button>
          <button 
            @click="handleAddUser" 
            class="px-4 py-2 bg-[#E30613] text-white rounded text-xs font-bold hover:bg-red-700 transition cursor-pointer"
          >
            Davet gönder
          </button>
        </div>
      </div>
    </div>

    <!-- AI RİSK & DÜZENLENEBİLİR GERİ BİLDİRİM MODALI -->
    <div 
      v-if="showFeedbackModal" 
      class="fixed inset-0 bg-[#031B39]/50 backdrop-blur-xs flex items-center justify-center z-50 p-4"
    >
      <div class="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-100 space-y-4">
        <div class="flex items-center justify-between pb-3 border-b border-slate-100">
          <h3 class="font-bold text-[#031B39] text-base flex items-center gap-2">
            <span>👎 Olumsuz Geri Bildirim Detayı</span>
          </h3>
          <button @click="showFeedbackModal = false" class="text-slate-400 hover:text-slate-600 text-lg cursor-pointer">✕</button>
        </div>

        <div class="space-y-3 text-xs">
          <div>
            <label class="font-bold text-slate-500 block mb-1">İncelenen Soru:</label>
            <p class="p-2.5 bg-slate-50 rounded-lg border border-slate-200 text-[#031B39] font-medium leading-relaxed">
              {{ activeLog?.question }}
            </p>
          </div>

          <div>
            <div class="flex items-center justify-between mb-1">
              <label class="font-bold text-[#031B39]">AI Tarafından Oluşturulan Nedeni Düzenleyebilirsiniz:</label>
              <span v-if="isGeneratingReason" class="text-[10px] text-[#E30613] font-semibold animate-pulse">AI Oluşturuyor...</span>
            </div>
            <textarea 
              v-model="editableReason"
              rows="4"
              placeholder="Olumsuz değerlendirme sebebini düzenleyin..."
              class="w-full p-3 bg-[#F0F2F5] border border-slate-200 rounded-xl text-xs text-[#031B39] focus:outline-none focus:border-[#E30613] leading-relaxed"
            ></textarea>
          </div>

          <div class="flex justify-end space-x-2 pt-2">
            <button 
              @click="showFeedbackModal = false" 
              class="px-4 py-2 bg-slate-100 text-slate-600 rounded-lg font-medium hover:bg-slate-200 cursor-pointer"
            >
              İptal
            </button>
            <button 
              @click="submitFeedback" 
              class="px-5 py-2 bg-[#E30613] hover:bg-[#c40510] text-white rounded-lg font-semibold shadow-xs transition cursor-pointer"
            >
              Düzenlemeyi Kaydet & Gönder →
            </button>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const activeTab = ref('users')
const searchQuery = ref('')
const roleFilter = ref('all')
const feedbackFilter = ref('flagged') // Varsayılan olarak AI'ın riskli bulduğu 20 sorguyu düşürür

const showProfileMenu = ref(false)
const showAddUserModal = ref(false)
const showFeedbackModal = ref(false)

const newUserEmail = ref('')
const newUserRole = ref('User')

const activeLog = ref(null)
const editableReason = ref('')
const isGeneratingReason = ref(false)

const users = ref([
  { initials: 'EY', name: 'Elif Yıldırım', email: 'elif.yildirim@kurum.gov.tr', role: 'Admin', status: 'Aktif', lastLogin: '12.08.2026 09:12' },
  { initials: 'MA', name: 'Mehmet Arslan', email: 'mehmet.arslan@kurum.gov.tr', role: 'Admin', status: 'Aktif', lastLogin: '12.08.2026 08:47' },
  { initials: 'ZK', name: 'Zeynep Koç', email: 'zeynep.koc@kurum.gov.tr', role: 'User', status: 'Aktif', lastLogin: '11.08.2026 17:30' },
  { initials: 'BD', name: 'Burak Demirtaş', email: 'burak.demirtas@kurum.gov.tr', role: 'User', status: 'Askıda', lastLogin: '09.08.2026 14:02' },
  { initials: 'AG', name: 'Ayşe Güneş', email: 'ayse.gunes@kurum.gov.tr', role: 'User', status: 'Aktif', lastLogin: '12.08.2026 10:05' },
  { initials: 'OŞ', name: 'Onur Şahin', email: 'onur.sahin@kurum.gov.tr', role: 'User', status: 'Aktif', lastLogin: '04.08.2026 11:41' },
  { initials: 'DA', name: 'Deniz Aksoy', email: 'deniz.aksoy@kurum.gov.tr', role: 'User', status: 'Davet bekliyor', lastLogin: '—' }
])

const documents = ref([
  { name: 'Personel Yönetmeliği 2026.pdf', pool: 'İnsan Kaynakları', status: 'INDEXED', chunk: '1.204', lastSync: '12.08.2026 09:40' },
  { name: 'Satın Alma Süreçleri v7.docx', pool: 'Mali İşler', status: 'INDEXED', chunk: '842', lastSync: '12.08.2026 09:40' },
  { name: 'BT Güvenlik Politikası.pdf', pool: 'Bilgi Teknolojileri', status: 'PENDING', chunk: '—', lastSync: '—' },
  { name: 'Arşiv Genelgeleri 2019-2024.zip', pool: 'Genel Arşiv', status: 'FAILED', chunk: '0', lastSync: '11.08.2026 23:18' },
  { name: 'Yıllık İzin Rehberi.pdf', pool: 'İnsan Kaynakları', status: 'INDEXED', chunk: '318', lastSync: '10.08.2026 09:40' },
  { name: 'Tedarikçi Sözleşmeleri Q2.pdf', pool: 'Mali İşler', status: 'INDEXED', chunk: '2.061', lastSync: '08.08.2026 09:40' },
  { name: 'Uzaktan Çalışma Talimatı.docx', pool: 'İnsan Kaynakları', status: 'PENDING', chunk: '—', lastSync: '—' }
])

const chartBars = ref([
  { height: 45, highlight: false },
  { height: 55, highlight: false },
  { height: 50, highlight: false },
  { height: 65, highlight: false },
  { height: 72, highlight: false },
  { height: 40, highlight: false },
  { height: 35, highlight: false },
  { height: 62, highlight: false },
  { height: 70, highlight: false },
  { height: 82, highlight: false },
  { height: 78, highlight: false },
  { height: 95, highlight: true },
  { height: 65, highlight: false },
  { height: 50, highlight: false }
])

const topTopics = ref([
  { title: 'İzin ve mesai', count: 412, percentage: 90 },
  { title: 'Satın alma', count: 308, percentage: 70 },
  { title: 'BT destek', count: 264, percentage: 58 },
  { title: 'Harcırah / ödeme', count: 171, percentage: 40 },
  { title: 'Arşiv erişimi', count: 96, percentage: 22 }
])

// AI Tarafından Ön Taramadan Geçirilmiş Log Kayıtları
const logs = ref([
  { 
    time: '09:58', 
    user: 'Onur Şahin', 
    question: 'Kurumsal VPN kurulumu nasıl yapılır?', 
    source: 'BT Güvenlik Politikası', 
    duration: '3,8s', 
    feedback: 'Olumsuz',
    isFlagged: true,
    riskLevel: 'Yüksek',
    aiRiskReason: 'Kurumsal BT güvenlik politikaları ihlali şüphesi. VPN kurulum ve erişim şifrelerinin yetkisiz paylaşımı tespiti.',
    reason: ''
  },
  { 
    time: '08:31', 
    user: 'Burak Demirtaş', 
    question: 'Arşiv talebi için hangi form kullanılır?', 
    source: 'Kaynak bulunamadı', 
    duration: '4,6s', 
    feedback: 'Olumsuz',
    isFlagged: true,
    riskLevel: 'Orta',
    aiRiskReason: 'Veritabanında ilgili soruya ait eşleşen mevzuat belgesi bulunamadı. Yetersiz içerik riski.',
    reason: ''
  },
  { 
    time: '10:38', 
    user: 'Ayşe Güneş', 
    question: 'Yıllık izin devri kaç güne kadar yapılabilir?', 
    source: 'Yıllık İzin Rehberi', 
    duration: '1,4s', 
    feedback: 'Olumlu',
    isFlagged: false,
    riskLevel: 'Düşük',
    aiRiskReason: '',
    reason: ''
  },
  { 
    time: '10:22', 
    user: 'Zeynep Koç', 
    question: 'Doğrudan temin limiti 2026 için ne kadar?', 
    source: 'Satın Alma v7', 
    duration: '2,1s', 
    feedback: 'Olumlu',
    isFlagged: false,
    riskLevel: 'Düşük',
    aiRiskReason: '',
    reason: ''
  },
  { 
    time: '09:41', 
    user: 'Mehmet Arslan', 
    question: 'Fazla mesai ödemesi hangi kalemden karşılanır?', 
    source: 'Personel Yönetmeliği', 
    duration: '1,7s', 
    feedback: 'Olumlu',
    isFlagged: false,
    riskLevel: 'Düşük',
    aiRiskReason: '',
    reason: ''
  }
])

const flaggedCount = computed(() => logs.value.filter(l => l.isFlagged).length)

const filteredUsers = computed(() => {
  return users.value.filter(u => {
    const matchesSearch = u.name.toLowerCase().includes(searchQuery.value.toLowerCase()) || 
                          u.email.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchesRole = roleFilter.value === 'all' || u.role.toLowerCase() === roleFilter.value
    return matchesSearch && matchesRole
  })
})

const filteredLogs = computed(() => {
  if (feedbackFilter.value === 'flagged') return logs.value.filter(l => l.isFlagged)
  if (feedbackFilter.value === 'positive') return logs.value.filter(l => l.feedback === 'Olumlu')
  if (feedbackFilter.value === 'negative') return logs.value.filter(l => l.feedback === 'Olumsuz')
  return logs.value
})

const handleLogout = () => {
  showProfileMenu.value = false
  router.push('/login')
}

const handleAddUser = () => {
  if (newUserEmail.value) {
    const namePart = newUserEmail.value.split('@')[0].replace('.', ' ')
    const formattedName = namePart.split(' ').map(w => w.charAt(0).toUpperCase() + w.slice(1)).join(' ')
    const initials = namePart.split(' ').map(w => w.charAt(0).toUpperCase()).join('')

    users.value.unshift({
      initials: initials || 'XK',
      name: formattedName || 'Yeni Kullanıcı',
      email: newUserEmail.value,
      role: newUserRole.value,
      status: 'Davet bekliyor',
      lastLogin: '—'
    })

    newUserEmail.value = ''
    showAddUserModal.value = false
  }
}

// 1. Olumlu (Temiz) İşaretleme
const markAsSafe = (log) => {
  log.feedback = 'Olumlu'
  log.isFlagged = false
  log.reason = 'Admin tarafından güvenli ilan edildi.'
  alert('Sorgu güvenli olarak işaretlendi ve risk listesinden temizlendi.')
}

// 2. Olumsuz (İncele) İnceleme Modalı Açma ve AI Gerekçesi Çağırma
const openFeedbackModal = async (log) => {
  activeLog.value = log
  showFeedbackModal.value = true

  if (log.aiRiskReason) {
    editableReason.value = log.aiRiskReason
  } else {
    isGeneratingReason.value = true
    editableReason.value = 'Yapay zeka olumsuzluk nedenini analiz ediyor...'
    
    try {
      const response = await fetch('http://127.0.0.1:8000/api/feedback/generate-reason', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          question: log.question,
          answer: 'Sistem yanıtı',
          source: log.source
        })
      })
      const data = await response.json()
      if (data.status === 'success') {
        editableReason.value = data.suggested_reason
      }
    } catch (e) {
      editableReason.value = 'Soruya verilen yanıt TİKA kurumsal bilgi ve mevzuat standartlarıyla örtüşmemektedir.'
    } finally {
      isGeneratingReason.value = false
    }
  }
}

// 3. Düzenlemeyi Kaydet ve Gönder
const submitFeedback = () => {
  if (activeLog.value) {
    activeLog.value.feedback = 'Olumsuz'
    activeLog.value.reason = editableReason.value
    activeLog.value.aiRiskReason = editableReason.value
    activeLog.value.isFlagged = false
  }
  showFeedbackModal.value = false
  alert('Geri bildirim ve düzenlenmiş olumsuzluk gerekçesi başarıyla sisteme kaydedildi!')
}
</script>