<template>
  <div class="min-h-screen bg-[#F3F4F6] flex flex-col text-[#1F2937] relative">
    <!-- Üst Bar (Header) -->
    <header class="h-16 bg-[#031B39] px-6 flex items-center justify-between z-20 shrink-0 shadow-md relative">
      <div class="flex items-center">
        <img
            src="../assets/logo.png"
            alt="TİKA AI Logo"
            class="h-16 w-auto object-contain select-none cursor-pointer"
        />
      </div>

      <div class="relative">
        <button
            @click="showProfileMenu = !showProfileMenu"
            class="flex items-center space-x-3 focus:outline-none cursor-pointer p-1 rounded hover:bg-slate-800/60 transition"
        >
          <div class="w-8 h-8 bg-slate-700 rounded flex items-center justify-center font-bold text-sm text-white border border-slate-500">
            AD
          </div>
          <div class="text-left text-xs">
            <div class="font-bold text-white leading-tight">Sistem Yöneticisi</div>
            <div class="text-slate-400 font-semibold text-[10px] tracking-wider">ADMIN</div>
          </div>
        </button>

        <div v-if="showProfileMenu" class="absolute right-0 mt-2 w-72 bg-white rounded-xl shadow-2xl border border-gray-100 z-50 overflow-hidden text-xs">
          <div class="p-4 space-y-2">
            <div class="font-bold text-gray-900 text-sm">Sistem Yöneticisi</div>
            <div class="text-gray-500 text-xs">admin@tika.gov.tr</div>
            <div class="inline-block bg-red-50 text-[#E30613] font-medium px-2.5 py-1 rounded-md text-[11px]">
              Department of Information Technology
            </div>
          </div>
          <div class="border-t border-gray-100 p-2">
            <button @click="handleLogout" class="w-full text-left px-3 py-2 text-red-600 font-semibold hover:bg-red-50 rounded-lg flex items-center space-x-2 transition cursor-pointer">
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
      <!-- Sol Menü -->
      <aside class="w-64 bg-[#EAEAEA] border-r border-gray-300 flex flex-col justify-between p-4">
        <div>
          <h2 class="text-xs font-bold text-gray-500 uppercase tracking-wider mb-4 px-2">YÖNETİM KONSOLU</h2>
          <nav class="space-y-1">
            <button @click="activeTab = 'users'" :class="activeTab === 'users' ? 'bg-[#1F2937] text-white' : 'text-gray-700 hover:bg-gray-300'" class="w-full text-left px-3 py-2 rounded text-sm font-semibold flex justify-between items-center transition cursor-pointer">
              <span>Kullanıcılar</span>
              <span class="text-xs opacity-75">{{ users.length }}</span>
            </button>

            <button @click="activeTab = 'vector'" :class="activeTab === 'vector' ? 'bg-[#1F2937] text-white' : 'text-gray-700 hover:bg-gray-300'" class="w-full text-left px-3 py-2 rounded text-sm font-semibold flex justify-between items-center transition cursor-pointer">
              <span>Vektör Veritabanı</span>
              <span class="text-xs opacity-75">{{ indexedDocsCount }}</span>
            </button>

            <button @click="activeTab = 'analytics'" :class="activeTab === 'analytics' ? 'bg-[#1F2937] text-white' : 'text-gray-700 hover:bg-gray-300'" class="w-full text-left px-3 py-2 rounded text-sm font-semibold flex justify-between items-center transition cursor-pointer">
              <span>Analitik & Loglar</span>
              <span class="text-xs opacity-75">{{ monthlyTokens }}</span>
            </button>
          </nav>
        </div>
      </aside>

      <div v-if="usersError" class="p-4 bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg mb-4">
        ⚠️ {{ usersError }}
      </div>
      <div v-else-if="users.length === 0" class="p-6 text-center text-gray-500 text-sm">
        Veritabanında henüz kullanıcı bulunmuyor.
      </div>

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
              <button @click="showAddUserModal = true" class="px-4 py-2 bg-[#E30613] text-white rounded text-sm font-semibold hover:bg-red-700 transition cursor-pointer">
                + Yeni kullanıcı
              </button>
            </div>
          </div>

          <div class="grid grid-cols-4 gap-4">
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">TOPLAM PERSONEL</div>
              <div class="text-3xl font-black text-black mt-1">{{ users.length }}</div>
            </div>
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">ADMİN</div>
              <div class="text-3xl font-black text-black mt-1">{{ adminCount }}</div>
            </div>
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">ASKIDA</div>
              <div class="text-3xl font-black text-black mt-1">{{ suspendedCount }}</div>
            </div>
            <div class="bg-[#E30613] text-white p-4 rounded shadow-sm">
              <div class="text-xs font-bold tracking-wider opacity-90">BEKLEYEN ŞİFRE TALEBİ</div>
              <div class="text-3xl font-black mt-1">0</div>
            </div>
          </div>

          <div class="grid grid-cols-3 gap-6">
            <div class="col-span-2 space-y-4">
              <div class="flex justify-between items-center gap-4">
                <input v-model="searchQuery" type="text" placeholder="Ad veya e-posta ara" class="flex-1 bg-gray-200 text-sm px-4 py-2 rounded border border-gray-300 focus:outline-none focus:border-gray-500" />
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
                        <span :class="user.role === 'Admin' ? 'bg-[#E30613] text-white' : 'bg-gray-200 text-gray-700'" class="px-2 py-0.5 rounded font-bold text-[10px]">
                          {{ user.role }}
                        </span>
                    </td>
                    <td class="p-3 text-[11px]">
                          <span class="flex items-center space-x-1.5">
                              <span :class="user.status === 'Aktif' ? 'bg-green-500' : 'bg-gray-400'" class="w-1.5 h-1.5 rounded-full"></span>
                              <span class="text-gray-900">{{ user.status }}</span>
                          </span>
                    </td>
                    <td class="p-3 text-gray-500 text-[11px]">{{ user.lastLogin }}</td>
                    <td class="p-3 text-right space-x-1">
                      <button v-if="user.status !== 'Askıda'" @click="suspendUser(user)" class="px-2 py-1 border border-gray-300 rounded text-[10px] font-semibold text-gray-700 hover:bg-gray-100 cursor-pointer">Askıya al</button>
                      <button v-else @click="suspendUser(user)" class="px-2 py-1 border border-gray-300 rounded text-[10px] font-semibold text-gray-700 hover:bg-gray-100 cursor-pointer">Geri al</button>
                    </td>
                  </tr>
                  </tbody>
                </table>
                <div v-if="users.length === 0" class="p-6 text-center text-gray-500 text-sm">
                  Veritabanında henüz kullanıcı bulunmuyor veya bağlantı sağlanamadı.
                </div>
              </div>
            </div>

            <!-- Sağ Taraf: Şifre Talepleri & Bilgi Havuzu -->
            <div class="space-y-6">
              <div class="bg-gray-100 p-4 rounded border border-gray-200 space-y-3">
                <span class="text-[10px] font-bold text-gray-400 tracking-wider uppercase">BİLGİ HAVUZU ERİŞİMİ</span>
                <div class="space-y-2 text-xs font-semibold">
                  <div class="flex justify-between items-center">
                    <span>İnsan Kaynakları</span>
                    <input type="checkbox" checked class="accent-red-600 w-4 h-4 cursor-pointer" />
                  </div>
                  <div class="flex justify-between items-center">
                    <span>Mali İşler</span>
                    <input type="checkbox" checked class="accent-red-600 w-4 h-4 cursor-pointer" />
                  </div>
                  <div class="flex justify-between items-center">
                    <span>Genel Arşiv</span>
                    <input type="checkbox" checked class="accent-red-600 w-4 h-4 cursor-pointer" />
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

          <div v-if="documentsError" class="p-4 bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg mb-4">
            ⚠️ {{ documentsError }}
          </div>

          <div class="grid grid-cols-4 gap-4">
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">INDEXED</div>
              <div class="text-3xl font-black text-black mt-1">{{ indexedDocsCount }}</div>
            </div>
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">PENDING</div>
              <div class="text-3xl font-black text-black mt-1">{{ pendingDocsCount }}</div>
            </div>
            <div class="bg-[#E30613] text-white p-4 rounded shadow-sm">
              <div class="text-xs font-bold tracking-wider opacity-90">FAILED</div>
              <div class="text-3xl font-black mt-1">{{ failedDocsCount }}</div>
            </div>
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">TOPLAM CHUNK</div>
              <div class="text-3xl font-black text-black mt-1">{{ totalChunksCount }}</div>
            </div>
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
                <td class="p-3 font-semibold">{{ doc.fileName }}</td>
                <td class="p-3 text-gray-500">TİKA Web</td>
                <td class="p-3">
                    <span :class="{
                      'text-green-700 font-bold': doc.status === 'ready',
                      'text-gray-500 font-bold': doc.status === 'processing',
                      'bg-[#E30613] text-white px-2 py-0.5 rounded': doc.status === 'failed'
                    }">
                      {{ doc.status.toUpperCase() }}
                    </span>
                </td>
                <td class="p-3 font-mono">{{ doc.chunkCount }}</td>
                <td class="p-3 text-gray-500">{{ doc.lastSync }}</td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- ANALİTİK & LOGLAR TAB'İ (sad uključuje i Risk Tespiti, dvije odvojene tabele) -->
        <div v-else-if="activeTab === 'analytics'" class="space-y-6">
          <div class="flex justify-between items-center">
            <div>
              <span class="text-[11px] font-bold text-red-600 tracking-wider uppercase">KULLANIM VE PERFORMANS</span>
              <h1 class="text-3xl font-extrabold text-black">Analitik ve Log Paneli</h1>
            </div>
          </div>

          <div v-if="analyticsError" class="p-4 bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg">
            ⚠️ {{ analyticsError }}
          </div>

          <div class="grid grid-cols-4 gap-4">
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">AYLIK TOKEN</div>
              <div class="text-3xl font-black text-black mt-1">{{ monthlyTokens }}</div>
            </div>
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">ORT. YANIT SÜRESİ</div>
              <div class="text-3xl font-black text-black mt-1">{{ avgResponseTime }}</div>
            </div>
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">AKTİF KULLANICI</div>
              <div class="text-3xl font-black text-black mt-1">{{ activeUsersCount }}</div>
            </div>
            <div class="bg-white p-4 border border-gray-200 rounded shadow-sm">
              <div class="text-xs font-bold text-gray-400 tracking-wider">MEMNUNİYET</div>
              <div class="text-3xl font-black text-black mt-1">{{ satisfactionRate }}</div>
            </div>
          </div>

          <div v-if="riskError" class="p-4 bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg">
            ⚠️ {{ riskError }}
          </div>

          <!-- İNCELEME BEKLEYENLER -->
          <div class="space-y-3">
            <div class="flex justify-between items-center">
              <span class="text-[10px] font-bold text-gray-400 tracking-wider uppercase">RİSK TESPİTİ — İNCELEME BEKLEYENLER</span>
              <span v-if="pendingFlags.length > 0" class="text-[10px] font-bold text-[#E30613] bg-red-50 px-2 py-0.5 rounded">{{ pendingFlags.length }} bekliyor</span>
            </div>

            <div class="bg-white border border-gray-200 rounded shadow-sm overflow-hidden">
              <table class="w-full text-left text-xs">
                <thead class="bg-gray-100 border-b border-gray-200 text-gray-500 font-bold uppercase tracking-wider">
                <tr>
                  <th class="p-3">ZAMAN</th>
                  <th class="p-3">KULLANICI</th>
                  <th class="p-3">SORU (PROMPT)</th>
                  <th class="p-3 text-right">İŞLEM</th>
                </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 font-medium text-gray-800">
                <tr v-for="flag in pendingFlags" :key="flag.messageId" class="hover:bg-gray-50 cursor-pointer" @click="selectedFlag = flag">
                  <td class="p-3 text-gray-500 font-mono text-[11px]">{{ flag.time }}</td>
                  <td class="p-3 font-bold text-gray-900">{{ flag.userEmail }}</td>
                  <td class="p-3 text-gray-800 font-semibold max-w-lg truncate">{{ flag.question }}</td>
                  <td class="p-3 text-right">
                    <span class="text-[10px] font-bold text-[#E30613]">İncele →</span>
                  </td>
                </tr>
                </tbody>
              </table>
              <div v-if="pendingFlags.length === 0" class="p-6 text-center text-gray-500 text-sm">
                Bekleyen risk incelemesi bulunmuyor.
              </div>
            </div>
          </div>

          <!-- İNCELENMİŞ / GEÇMİŞ -->
          <div class="space-y-3">
            <div class="flex justify-between items-center">
              <span class="text-[10px] font-bold text-gray-400 tracking-wider uppercase">RİSK TESPİTİ — İNCELENMİŞ GEÇMİŞ</span>
            </div>

            <div class="bg-white border border-gray-200 rounded shadow-sm overflow-hidden">
              <table class="w-full text-left text-xs">
                <thead class="bg-gray-100 border-b border-gray-200 text-gray-500 font-bold uppercase tracking-wider">
                <tr>
                  <th class="p-3">ZAMAN</th>
                  <th class="p-3">KULLANICI</th>
                  <th class="p-3">SORU (PROMPT)</th>
                  <th class="p-3 text-right">DURUM</th>
                </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 font-medium text-gray-800">
                <tr v-for="flag in reviewedFlags" :key="flag.messageId" class="hover:bg-gray-50 cursor-pointer" @click="selectedFlag = flag">
                  <td class="p-3 text-gray-500 font-mono text-[11px]">{{ flag.time }}</td>
                  <td class="p-3 font-bold text-gray-900">{{ flag.userEmail }}</td>
                  <td class="p-3 text-gray-800 font-semibold max-w-lg truncate">{{ flag.question }}</td>
                  <td class="p-3 text-right text-[11px]" :class="flag.riskStatus === 'confirmed' ? 'text-red-600 font-bold' : 'text-gray-400 italic'">
                    {{ flag.riskStatus === 'confirmed' ? '🔴 Risk' : '✓ Temiz' }}
                  </td>
                </tr>
                </tbody>
              </table>
              <div v-if="reviewedFlags.length === 0" class="p-6 text-center text-gray-500 text-sm">
                Henüz incelenmiş kayıt bulunmuyor.
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- YENİ KULLANICI EKLE MODAL -->
    <div v-if="showAddUserModal" class="fixed inset-0 bg-black/40 backdrop-blur-xs flex items-center justify-center z-50 p-4">
      <div class="bg-[#EAEAEA] rounded-lg shadow-2xl w-full max-w-md p-6 space-y-5 border border-gray-300">
        <h2 class="text-xl font-extrabold text-gray-900">Yeni kullanıcı ekle</h2>
        <div class="space-y-4 text-xs">
          <div>
            <label class="block font-semibold text-gray-600 mb-1">Kurumsal e-posta</label>
            <input v-model="newUserEmail" type="email" placeholder="ad.soyad@kurum.gov.tr" class="w-full bg-white border border-gray-300 rounded px-3 py-2 text-gray-800 focus:outline-none focus:border-gray-500 text-sm" />
          </div>
          <div>
            <label class="block font-semibold text-gray-600 mb-1">Rol</label>
            <div class="inline-flex border border-gray-300 rounded overflow-hidden">
              <button @click="newUserRole = 'User'" :class="newUserRole === 'User' ? 'bg-[#E30613] text-white font-bold' : 'bg-gray-200 text-gray-700'" class="px-4 py-1.5 transition cursor-pointer">User</button>
              <button @click="newUserRole = 'Admin'" :class="newUserRole === 'Admin' ? 'bg-[#E30613] text-white font-bold' : 'bg-gray-200 text-gray-700'" class="px-4 py-1.5 border-l border-gray-300 transition cursor-pointer">Admin</button>
            </div>
          </div>
        </div>
        <div class="flex justify-end space-x-3 pt-2">
          <button @click="showAddUserModal = false" class="px-4 py-2 border border-gray-300 rounded text-xs font-bold text-gray-700 hover:bg-gray-200 transition cursor-pointer">Vazgeç</button>
          <button @click="handleAddUser" class="px-4 py-2 bg-[#E30613] text-white rounded text-xs font-bold hover:bg-red-700 transition cursor-pointer">Davet gönder</button>
        </div>
      </div>
    </div>

    <!-- RİSK İNCELEME MODAL -->
    <div v-if="selectedFlag" class="fixed inset-0 bg-black/40 backdrop-blur-xs flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-lg shadow-2xl w-full max-w-lg p-6 space-y-4">
        <div class="flex justify-between items-start">
          <div>
            <span class="text-[11px] font-bold text-red-600 uppercase">Geri Bildirim Detayı</span>
            <h3 class="text-lg font-bold text-gray-900">Risk Tespiti İncelemesi</h3>
          </div>
          <button @click="selectedFlag = null" class="text-gray-400 hover:text-gray-600 cursor-pointer">✕</button>
        </div>

        <div class="grid grid-cols-2 gap-3 text-xs">
          <div>
            <div class="text-gray-400 font-bold uppercase text-[10px]">Kullanıcı</div>
            <div class="font-semibold">{{ selectedFlag.userEmail }}</div>
          </div>
          <div>
            <div class="text-gray-400 font-bold uppercase text-[10px]">Zaman</div>
            <div class="font-semibold">{{ selectedFlag.time }}</div>
          </div>
        </div>

        <div>
          <div class="text-gray-400 font-bold uppercase text-[10px] mb-1">Sorulan Prompt</div>
          <div class="bg-red-50 border border-red-100 rounded p-3 text-xs text-red-800">{{ selectedFlag.question }}</div>
        </div>

        <!-- Ako je već pregledan, samo prikaži status bez dugmadi za akciju -->
        <div v-if="selectedFlag.riskStatus !== 'flagged'" class="pt-2">
          <div class="text-center text-xs font-bold py-2 rounded" :class="selectedFlag.riskStatus === 'confirmed' ? 'bg-red-50 text-red-700' : 'bg-gray-100 text-gray-600'">
            {{ selectedFlag.riskStatus === 'confirmed' ? '🔴 Risk olarak onaylandı' : '✓ Temiz olarak işaretlendi' }}
          </div>
        </div>
        <div v-else class="flex gap-3 pt-2">
          <button @click="reviewFlag(selectedFlag.messageId, false)" class="flex-1 py-2 border border-gray-300 rounded text-xs font-bold text-gray-700 hover:bg-gray-100 cursor-pointer">
            ✗ Temiz (Yanlış Alarm)
          </button>
          <button @click="reviewFlag(selectedFlag.messageId, true)" class="flex-1 py-2 bg-[#E30613] text-white rounded text-xs font-bold hover:bg-red-700 cursor-pointer">
            ✓ Risk Onayla
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from '../composables/useToast'

const router = useRouter()
const { showToast } = useToast()

const activeTab = ref('users')
const searchQuery = ref('')
const roleFilter = ref('all')
const usersError = ref(null)
const documentsError = ref(null)
const analyticsError = ref(null)
const riskError = ref(null)

const showProfileMenu = ref(false)
const showAddUserModal = ref(false)

const newUserEmail = ref('')
const newUserRole = ref('User')

// -- VERİ DURUMLARI (STATE) --
const users = ref([])
const documents = ref([])
const riskFlags = ref([])
const selectedFlag = ref(null)

// Analitik İstatistikleri
const monthlyTokens = ref('0')
const avgResponseTime = ref('0s')
const activeUsersCount = ref(0)
const satisfactionRate = ref('%0')

// Vektör İstatistikleri
const indexedDocsCount = ref(0)
const pendingDocsCount = ref(0)
const failedDocsCount = ref(0)
const totalChunksCount = ref(0)

// Computed
const adminCount = computed(() => users.value.filter(u => u.role === 'Admin').length)
const suspendedCount = computed(() => users.value.filter(u => u.status === 'Askıda').length)

const filteredUsers = computed(() => {
  return users.value.filter(u => {
    const matchesSearch = u.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
        u.email.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchesRole = roleFilter.value === 'all' || u.role.toLowerCase() === roleFilter.value
    return matchesSearch && matchesRole
  })
})

// Risk flagovi podijeljeni na dvije liste
const pendingFlags = computed(() => riskFlags.value.filter(f => f.riskStatus === 'flagged'))
const reviewedFlags = computed(() => riskFlags.value.filter(f => f.riskStatus === 'confirmed' || f.riskStatus === 'clean'))

// -- FETCH FONKSİYONLARI --

const fetchUsers = async () => {
  usersError.value = null
  try {
    const response = await fetch('http://localhost:8080/api/admin/users', {
      headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
    })
    if (!response.ok) {
      const data = await response.json().catch(() => null)
      throw new Error(data?.message || `Sunucu hatası: ${response.status}`)
    }
    const data = await response.json()
    users.value = data.map(u => {
      const nameParts = (u.fullName || u.email).split(' ')
      return {
        id: u.id,
        initials: (nameParts[0]?.[0] || 'U') + (nameParts[1]?.[0] || ''),
        name: u.fullName || u.username || u.email,
        email: u.email,
        role: u.userRole === 'admin' ? 'Admin' : 'User',
        status: u.isActive ? 'Aktif' : 'Askıda',
        lastLogin: u.lastLogin,
      }
    })
  } catch (error) {
    console.error("Kullanıcılar çekilemedi:", error)
    usersError.value = error.message || "Kullanıcılar yüklenemedi. Lütfen sunucu bağlantısını kontrol edin."
    users.value = []
  }
}

const fetchDocuments = async () => {
  documentsError.value = null
  try {
    const response = await fetch('http://localhost:8080/api/admin/vector-status', {
      headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
    })
    if (!response.ok) {
      const data = await response.json().catch(() => null)
      throw new Error(data?.message || `Sunucu hatası: ${response.status}`)
    }
    const result = await response.json()
    documents.value = result.documents
    indexedDocsCount.value = result.indexed
    pendingDocsCount.value = result.pending
    failedDocsCount.value = result.failed
    totalChunksCount.value = result.totalChunks
  } catch (error) {
    console.error("Dokümanlar çekilemedi:", error)
    documentsError.value = error.message || "Vektör veritabanı bilgileri yüklenemedi."
    documents.value = []
    indexedDocsCount.value = 0
    pendingDocsCount.value = 0
    failedDocsCount.value = 0
    totalChunksCount.value = 0
  }
}

const fetchAnalytics = async () => {
  analyticsError.value = null
  try {
    const response = await fetch('http://localhost:8080/api/admin/analytics', {
      headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
    })
    if (!response.ok) {
      const data = await response.json().catch(() => null)
      throw new Error(data?.message || `Sunucu hatası: ${response.status}`)
    }
    const result = await response.json()
    monthlyTokens.value = result.monthlyTokens ?? 0
    avgResponseTime.value = result.avgResponseTimeSeconds ? `${result.avgResponseTimeSeconds.toFixed(1)}s` : '0s'
    activeUsersCount.value = result.activeUsers ?? 0
    satisfactionRate.value = result.satisfactionPct != null ? `%${result.satisfactionPct}` : '%0'
  } catch (error) {
    console.error("Analitik veriler çekilemedi:", error)
    analyticsError.value = error.message || "Analitik veriler yüklenemedi. Lütfen sunucu bağlantısını kontrol edin."
    monthlyTokens.value = 0
    avgResponseTime.value = '0s'
    activeUsersCount.value = 0
    satisfactionRate.value = '%0'
  }
}

const fetchRiskFlags = async () => {
  riskError.value = null
  try {
    const response = await fetch('http://localhost:8080/api/admin/risk-flags', {
      headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
    })
    if (!response.ok) {
      const data = await response.json().catch(() => null)
      throw new Error(data?.message || `Sunucu hatası: ${response.status}`)
    }
    riskFlags.value = await response.json()
  } catch (error) {
    console.error("Risk flagları çekilemedi:", error)
    riskError.value = error.message || "Risk tespitleri yüklenemedi."
    riskFlags.value = []
  }
}

const reviewFlag = async (messageId, isRisky) => {
  try {
    const response = await fetch(`http://localhost:8080/api/admin/risk-flags/${messageId}/review`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify({ isRisky })
    })
    if (response.ok) {
      const flagEntry = riskFlags.value.find(f => f.messageId === messageId)
      if (flagEntry) flagEntry.riskStatus = isRisky ? 'confirmed' : 'clean'
      selectedFlag.value = null
    } else {
      const data = await response.json().catch(() => null)
      showToast(data?.message || 'İşlem başarısız oldu.', 'error')
    }
  } catch (e) {
    showToast('Sunucuya ulaşılamadı.', 'error')
  }
}

const suspendUser = async (user) => {
  try {
    const response = await fetch(`http://localhost:8080/api/admin/users/${user.id}/deactivate`, {
      method: 'PATCH',
      headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.ok) {
      const index = users.value.findIndex(u => u.id === user.id);
      if (index !== -1) {
        users.value[index].status = users.value[index].status === 'Aktif' ? 'Askıda' : 'Aktif';
      }
    } else {
      const data = await response.json().catch(() => null)
      showToast(data?.message || 'İşlem başarısız oldu.', 'error')
    }
  } catch (e) {
    showToast('Sunucuya ulaşılamadı.', 'error')
  }
}

onMounted(() => {
  fetchUsers()
  fetchDocuments()
  fetchAnalytics()
  fetchRiskFlags()
})

const handleLogout = () => {
  showProfileMenu.value = false
  localStorage.removeItem('token')
  router.push('/login')
}

const handleAddUser = async () => {
  if (!newUserEmail.value) return
  const emailToInvite = newUserEmail.value

  showAddUserModal.value = false
  newUserEmail.value = ''

  try {
    const response = await fetch('http://localhost:8080/api/admin/invite', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token') || ''}`
      },
      body: JSON.stringify({ email: emailToInvite, role: newUserRole.value })
    })

    if (response.ok) {
      showToast(`Davet e-postası başarıyla gönderildi!`, 'success')
      fetchUsers()
    } else {
      const data = await response.json().catch(() => null)
      showToast(data?.message || 'Davet gönderilemedi. Lütfen Java sunucusunu kontrol edin.', 'error')
    }
  } catch (error) {
    console.error('Bağlantı hatası:', error)
    showToast('Sunucuya bağlanılamadı. Docker servislerinin açık olduğundan emin olun.', 'error')
  }
}
</script>