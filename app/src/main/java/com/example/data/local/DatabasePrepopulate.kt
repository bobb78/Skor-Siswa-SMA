package com.example.data.local

import com.example.data.model.Student
import com.example.data.model.ViolationCategory
import com.example.data.model.ViolationMaster
import com.example.data.model.ViolationRecord

object DatabasePrepopulate {

    fun getDefaultCategories(): List<ViolationCategory> = listOf(
        ViolationCategory(
            id = 1,
            code = "DIS",
            name = "Kedisiplinan & Waktu",
            iconName = "schedule",
            colorHex = 0xFFF57C00 // Amber Orange
        ),
        ViolationCategory(
            id = 2,
            code = "KRP",
            name = "Kerapian, Seragam & Penampilan",
            iconName = "checkroom",
            colorHex = 0xFF0288D1 // Light Blue
        ),
        ViolationCategory(
            id = 3,
            code = "TTB",
            name = "Ketertiban & Fasilitas Sekolah",
            iconName = "menu_book",
            colorHex = 0xFF7B1FA2 // Purple
        ),
        ViolationCategory(
            id = 4,
            code = "PRL",
            name = "Perilaku, Etika & Moralitas",
            iconName = "psychology",
            colorHex = 0xFFD32F2F // Red
        ),
        ViolationCategory(
            id = 5,
            code = "BRT",
            name = "Pelanggaran Berat, Asusila & Hukum",
            iconName = "warning",
            colorHex = 0xFF4A148C // Deep Purple
        )
    )

    /**
     * Official 37 Violation Rules & Points from School Disciplinary Code
     */
    fun getDefaultViolationMasters(): List<ViolationMaster> = listOf(
        // 1. Terlambat dalam mengikuti proses belajar mengajar (10)
        ViolationMaster(
            id = 1,
            categoryId = 1,
            categoryName = "Kedisiplinan & Waktu",
            code = "P-01",
            title = "Terlambat dalam mengikuti proses belajar mengajar",
            points = 10,
            defaultSanction = "Pencatatan buku piket & teguran lisan"
        ),
        // 2. Memakai seragam sekolah yang atributnya tidak lengkap atau tidak sesuai dengan ketentuan (nama tag,lokasi Sekolah,topi dan dasi) (10)
        ViolationMaster(
            id = 2,
            categoryId = 2,
            categoryName = "Kerapian, Seragam & Penampilan",
            code = "P-02",
            title = "Memakai seragam sekolah yang atributnya tidak lengkap atau tidak sesuai dengan ketentuan (nama tag, lokasi Sekolah, topi dan dasi)",
            points = 10,
            defaultSanction = "Teguran lisan & melengkapi atribut seragam"
        ),
        // 3. Rok atau celana sempit dan ketat (10)
        ViolationMaster(
            id = 3,
            categoryId = 2,
            categoryName = "Kerapian, Seragam & Penampilan",
            code = "P-03",
            title = "Rok atau celana sempit dan ketat",
            points = 10,
            defaultSanction = "Peringatan & penertiban pakaian sesuai standar sekolah"
        ),
        // 4. Memakai sepatu tidak sesuai peraturan (10)
        ViolationMaster(
            id = 4,
            categoryId = 2,
            categoryName = "Kerapian, Seragam & Penampilan",
            code = "P-04",
            title = "Memakai sepatu tidak sesuai peraturan",
            points = 10,
            defaultSanction = "Peringatan & pemakaian sepatu sesuai standar"
        ),
        // 5. Sering minta izin keluar pada saat jam pelajaran (10)
        ViolationMaster(
            id = 5,
            categoryId = 1,
            categoryName = "Kedisiplinan & Waktu",
            code = "P-05",
            title = "Sering minta izin keluar pada saat jam pelajaran",
            points = 10,
            defaultSanction = "Teguran guru mata pelajaran & pembatasan izin keluar"
        ),
        // 6. Sering keluar kelas pada saat pergantian jam pelajaran (10)
        ViolationMaster(
            id = 6,
            categoryId = 1,
            categoryName = "Kedisiplinan & Waktu",
            code = "P-06",
            title = "Sering keluar kelas pada saat pergantian jam pelajaran",
            points = 10,
            defaultSanction = "Teguran lisan oleh guru piket & pengawasan jam"
        ),
        // 7. Baju keluar tidak dimasukan dengan rapi (10)
        ViolationMaster(
            id = 7,
            categoryId = 2,
            categoryName = "Kerapian, Seragam & Penampilan",
            code = "P-07",
            title = "Baju keluar tidak dimasukan dengan rapi",
            points = 10,
            defaultSanction = "Merapikan pakaian langsung di tempat"
        ),
        // 8. Mengganggu proses belajar mengajar (10)
        ViolationMaster(
            id = 8,
            categoryId = 3,
            categoryName = "Ketertiban & Fasilitas Sekolah",
            code = "P-08",
            title = "Mengganggu proses belajar mengajar",
            points = 10,
            defaultSanction = "Pembinaan & peneguran oleh guru mata pelajaran"
        ),
        // 9. Terlambat masuk sesudah istirahat dan pertukaran jam (10)
        ViolationMaster(
            id = 9,
            categoryId = 1,
            categoryName = "Kedisiplinan & Waktu",
            code = "P-09",
            title = "Terlambat masuk sesudah istirahat dan pertukaran jam",
            points = 10,
            defaultSanction = "Teguran guru piket & pencatatan keterlambatan"
        ),
        // 10. Memakai baju dalam selain singlet (15)
        ViolationMaster(
            id = 10,
            categoryId = 2,
            categoryName = "Kerapian, Seragam & Penampilan",
            code = "P-10",
            title = "Memakai baju dalam selain singlet",
            points = 15,
            defaultSanction = "Teguran & penertiban pakaian dalam sesuai aturan"
        ),
        // 11. Membuang sampah atau isi makanan disembarang tempat (15)
        ViolationMaster(
            id = 11,
            categoryId = 3,
            categoryName = "Ketertiban & Fasilitas Sekolah",
            code = "P-11",
            title = "Membuang sampah atau isi makanan disembarang tempat",
            points = 15,
            defaultSanction = "Membersihkan area lingkungan sekolah / kelas"
        ),
        // 12. Surat izin lebih dari tiga kali dengan alasan tidak jelas (25)
        ViolationMaster(
            id = 12,
            categoryId = 1,
            categoryName = "Kedisiplinan & Waktu",
            code = "P-12",
            title = "Surat izin lebih dari tiga kali dengan alasan tidak jelas",
            points = 25,
            defaultSanction = "Konfirmasi langsung wali kelas ke orang tua siswa"
        ),
        // 13. Absen pada satu mata pelajaran berturut-turut tiga kali (25)
        ViolationMaster(
            id = 13,
            categoryId = 1,
            categoryName = "Kedisiplinan & Waktu",
            code = "P-13",
            title = "Absen pada satu mata pelajaran berturut-turut tiga kali",
            points = 25,
            defaultSanction = "Pemanggilan siswa & konfirmasi langsung wali murid"
        ),
        // 14. Menerima tamu tanpa seizin guru piket (25)
        ViolationMaster(
            id = 14,
            categoryId = 3,
            categoryName = "Ketertiban & Fasilitas Sekolah",
            code = "P-14",
            title = "Menerima tamu tanpa seizin guru piket",
            points = 25,
            defaultSanction = "Teguran keras & pencatatan di pos piket"
        ),
        // 15. Memelihara kuku panjang, memakai make up yang berlebihan(norak), mewarnai kuku, dan memakai perhiasan selain anting-anting dan jam tangan bagi siswa perempuan (50)
        ViolationMaster(
            id = 15,
            categoryId = 2,
            categoryName = "Kerapian, Seragam & Penampilan",
            code = "P-15",
            title = "Memelihara kuku panjang, memakai make up yang berlebihan(norak), mewarnai kuku, dan memakai perhiasan selain anting-anting dan jam tangan bagi siswa perempuan",
            points = 50,
            defaultSanction = "Pemotongan kuku, pembersihan riasan & penyitaan perhiasan"
        ),
        // 16. Memakai anting-anting, kalung, gelang, dan cincin bagi siswa laki-laki (50)
        ViolationMaster(
            id = 16,
            categoryId = 2,
            categoryName = "Kerapian, Seragam & Penampilan",
            code = "P-16",
            title = "Memakai anting-anting, kalung, gelang, dan cincin bagi siswa laki-laki",
            points = 50,
            defaultSanction = "Penyitaan aksesoris & pembinaan wali kelas"
        ),
        // 17. Berambut panjang dan tidak sesuai ketentuan bagi laki-laki (50)
        ViolationMaster(
            id = 17,
            categoryId = 2,
            categoryName = "Kerapian, Seragam & Penampilan",
            code = "P-17",
            title = "Berambut panjang dan tidak sesuai ketentuan bagi laki-laki",
            points = 50,
            defaultSanction = "Peringatan & pemotongan rambut rapi maksimal 2 hari"
        ),
        // 18. Membawa alat musik selain digunakan untuk praktik pembelajaran (50)
        ViolationMaster(
            id = 18,
            categoryId = 3,
            categoryName = "Ketertiban & Fasilitas Sekolah",
            code = "P-18",
            title = "Membawa alat musik selain digunakan untuk praktik pembelajaran",
            points = 50,
            defaultSanction = "Pengamanan alat musik di ruang BK/kesiswaan"
        ),
        // 19. Merusak atau menghilangkan buku paket perpustakaan(diwajibkan mengganti buku tersebut) (100)
        ViolationMaster(
            id = 19,
            categoryId = 3,
            categoryName = "Ketertiban & Fasilitas Sekolah",
            code = "P-19",
            title = "Merusak atau menghilangkan buku paket perpustakaan(diwajibkan mengganti buku tersebut)",
            points = 100,
            defaultSanction = "Wajib mengganti buku paket perpustakaan & pembinaan"
        ),
        // 20. Bolos, keluar, dan cabut dari jam pelajaran (100)
        ViolationMaster(
            id = 20,
            categoryId = 1,
            categoryName = "Kedisiplinan & Waktu",
            code = "P-20",
            title = "Bolos, keluar, dan cabut dari jam pelajaran",
            points = 100,
            defaultSanction = "Surat Peringatan & Notifikasi resmi ke orang tua"
        ),
        // 21. Melindungi teman, siswa lain di dalam melakukan kesalahan,berbohong dan melakukan sumpah palsu (100)
        ViolationMaster(
            id = 21,
            categoryId = 4,
            categoryName = "Perilaku, Etika & Moralitas",
            code = "P-21",
            title = "Melindungi teman, siswa lain di dalam melakukan kesalahan, berbohong dan melakukan sumpah palsu",
            points = 100,
            defaultSanction = "Konseling bimbingan BK & surat pernyataan tertulis"
        ),
        // 22. Mewarnai rambut bagi laki-laki (100)
        ViolationMaster(
            id = 22,
            categoryId = 2,
            categoryName = "Kerapian, Seragam & Penampilan",
            code = "P-22",
            title = "Mewarnai rambut bagi laki-laki",
            points = 100,
            defaultSanction = "Wajib menghitamkan kembali warna rambut & pembinaan BK"
        ),
        // 23. Berpacaran di lingkungan sekolah (200)
        ViolationMaster(
            id = 23,
            categoryId = 4,
            categoryName = "Perilaku, Etika & Moralitas",
            code = "P-23",
            title = "Berpacaran di lingkungan sekolah",
            points = 200,
            defaultSanction = "Pemanggilan kedua belah pihak & konseling intensif BK"
        ),
        // 24. Berjudi (200)
        ViolationMaster(
            id = 24,
            categoryId = 4,
            categoryName = "Perilaku, Etika & Moralitas",
            code = "P-24",
            title = "Berjudi",
            points = 200,
            defaultSanction = "Penyitaan barang bukti, SP 2 & Panggilan Orang Tua"
        ),
        // 25. Melakukan perundungan dan terlibat perundungan (250)
        ViolationMaster(
            id = 25,
            categoryId = 4,
            categoryName = "Perilaku, Etika & Moralitas",
            code = "P-25",
            title = "Melakukan perundungan dan terlibat perundungan",
            points = 250,
            defaultSanction = "SP 2 / SP 3, Panggilan Orang Tua & Skorsing Pembinaan"
        ),
        // 26. Membawa rokok, merokok, dan menjual rokok di lingkungan sekolah (250)
        ViolationMaster(
            id = 26,
            categoryId = 4,
            categoryName = "Perilaku, Etika & Moralitas",
            code = "P-26",
            title = "Membawa rokok, merokok, dan menjual rokok di lingkungan sekolah",
            points = 250,
            defaultSanction = "Penyitaan rokok, SP 2, Panggilan Orang Tua & Skorsing"
        ),
        // 27. Membawa senjata tajam ke sekolah tanpa izin (300)
        ViolationMaster(
            id = 27,
            categoryId = 5,
            categoryName = "Pelanggaran Berat, Asusila & Hukum",
            code = "P-27",
            title = "Membawa senjata tajam ke sekolah tanpa izin",
            points = 300,
            defaultSanction = "Penyitaan sajam, SP 3 & Panggilan Mendesak Wali Murid"
        ),
        // 28. Merusak kendaraan siswa, guru, dan pegawai sekolah(diwajibkan mengganti) (300)
        ViolationMaster(
            id = 28,
            categoryId = 3,
            categoryName = "Ketertiban & Fasilitas Sekolah",
            code = "P-28",
            title = "Merusak kendaraan siswa, guru, dan pegawai sekolah(diwajibkan mengganti)",
            points = 300,
            defaultSanction = "Wajib ganti rugi perbaikan kendaraan, SP 3 & Panggilan Orang Tua"
        ),
        // 29. Terlibat dan melakukan adu domba, provokasi, dan mogok belajar (300)
        ViolationMaster(
            id = 29,
            categoryId = 4,
            categoryName = "Perilaku, Etika & Moralitas",
            code = "P-29",
            title = "Terlibat dan melakukan adu domba, provokasi, dan mogok belajar",
            points = 300,
            defaultSanction = "SP 3, Skorsing & Konferensi Kasus Kesiswaan"
        ),
        // 30. Perbuatan asusila dilingkungan dan luar sekolah (500)
        ViolationMaster(
            id = 30,
            categoryId = 5,
            categoryName = "Pelanggaran Berat, Asusila & Hukum",
            code = "P-30",
            title = "Perbuatan asusila dilingkungan dan luar sekolah",
            points = 500,
            defaultSanction = "SP 3 / Konferensi Kasus Dewan Guru & Skorsing Berat"
        ),
        // 31. Pemerasan/berkelahi/mengeroyok dan terlibat tawuran sesama siswa/orang lain (500)
        ViolationMaster(
            id = 31,
            categoryId = 5,
            categoryName = "Pelanggaran Berat, Asusila & Hukum",
            code = "P-31",
            title = "Pemerasan/berkelahi/mengeroyok dan terlibat tawuran sesama siswa/orang lain",
            points = 500,
            defaultSanction = "Skorsing, SP 3 & Surat Perjanjian Terakhir Bermaterai"
        ),
        // 32. Memiliki/menyimpan/membawa/membuat bacaan, gambar, dan video bersifat pornografi yang mencemarkan nama baik sekolah di masyarakat (500)
        ViolationMaster(
            id = 32,
            categoryId = 5,
            categoryName = "Pelanggaran Berat, Asusila & Hukum",
            code = "P-32",
            title = "Memiliki/menyimpan/membawa/membuat bacaan, gambar, dan video bersifat pornografi yang mencemarkan nama baik sekolah di masyarakat",
            points = 500,
            defaultSanction = "Penyitaan HP/media, Konferensi Kasus & Pembinaan Khusus"
        ),
        // 33. Terlibat aksi pemerasan/pencurian/perampokan/penjamretan (500)
        ViolationMaster(
            id = 33,
            categoryId = 5,
            categoryName = "Pelanggaran Berat, Asusila & Hukum",
            code = "P-33",
            title = "Terlibat aksi pemerasan/pencurian/perampokan/penjamretan",
            points = 500,
            defaultSanction = "Konferensi Kasus, Ganti Rugi & Tindak Lanjut Kesiswaan"
        ),
        // 34. Merusak citra sekolah, nama sekolah, baik itu tulisan, lisan dan perbuatan (500)
        ViolationMaster(
            id = 34,
            categoryId = 4,
            categoryName = "Perilaku, Etika & Moralitas",
            code = "P-34",
            title = "Merusak citra sekolah, nama sekolah, baik itu tulisan, lisan dan perbuatan",
            points = 500,
            defaultSanction = "Surat Peringatan Keras, Pemanggilan Orang Tua & Sanksi Sosial"
        ),
        // 35. Terlibat atau melakukan pemerkosaan (1000)
        ViolationMaster(
            id = 35,
            categoryId = 5,
            categoryName = "Pelanggaran Berat, Asusila & Hukum",
            code = "P-35",
            title = "Terlibat atau melakukan pemerkosaan",
            points = 1000,
            defaultSanction = "Konferensi Kasus Dewan Guru & Pengembalian Siswa ke Orang Tua (DO)"
        ),
        // 36. Berkelahi,menghasut,memfitnah, melecehkan, dan memukul (guru dan pegawai sekolah) (1000)
        ViolationMaster(
            id = 36,
            categoryId = 5,
            categoryName = "Pelanggaran Berat, Asusila & Hukum",
            code = "P-36",
            title = "Berkelahi, menghasut, memfitnah, melecehkan, dan memukul (guru dan pegawai sekolah)",
            points = 1000,
            defaultSanction = "Konferensi Kasus Dewan Guru & Pengembalian Siswa ke Orang Tua (DO)"
        ),
        // 37. Terlibat Memiliki, menyimpan, menggunakan dan mengedarkan narkoba dan minuman keras (1000)
        ViolationMaster(
            id = 37,
            categoryId = 5,
            categoryName = "Pelanggaran Berat, Asusila & Hukum",
            code = "P-37",
            title = "Terlibat Memiliki, menyimpan, menggunakan dan mengedarkan narkoba dan minuman keras",
            points = 1000,
            defaultSanction = "Konferensi Kasus Dewan Guru & Pengembalian Siswa ke Orang Tua (DO)"
        )
    )

    fun getDefaultStudents(): List<Student> = emptyList()

    fun getDefaultRecords(): List<ViolationRecord> = emptyList()

    private fun getUnusedDefaultRecords(): List<ViolationRecord> {
        val now = System.currentTimeMillis()
        val dayMs = 86400000L
        return listOf(
            // Achmad Fauzan (Total 270)
            ViolationRecord(
                id = 1,
                studentId = 1,
                studentName = "Achmad Fauzan Pratama",
                studentNis = "20261001",
                studentClass = "X-MIPA 1",
                violationMasterId = 26,
                violationTitle = "Membawa rokok, merokok, dan menjual rokok di lingkungan sekolah",
                categoryName = "Perilaku, Etika & Moralitas",
                points = 250,
                timestamp = now - dayMs * 3,
                notes = "Tertangkap membawa dan merokok di belakang kantin.",
                reporterTeacher = "Bpk. Wahyu (Kesiswaan)",
                parentNotified = true,
                parentNotificationType = "WhatsApp",
                parentNotificationDate = now - dayMs * 3,
                status = "Dalam Pembinaan"
            ),
            ViolationRecord(
                id = 2,
                studentId = 1,
                studentName = "Achmad Fauzan Pratama",
                studentNis = "20261001",
                studentClass = "X-MIPA 1",
                violationMasterId = 1,
                violationTitle = "Terlambat dalam mengikuti proses belajar mengajar",
                categoryName = "Kedisiplinan & Waktu",
                points = 10,
                timestamp = now - dayMs * 2,
                notes = "Terlambat 15 menit pada jam pelajaran pertama.",
                reporterTeacher = "Ibu Ratna (Piket)",
                parentNotified = false,
                status = "Tercatat"
            ),
            ViolationRecord(
                id = 3,
                studentId = 1,
                studentName = "Achmad Fauzan Pratama",
                studentNis = "20261001",
                studentClass = "X-MIPA 1",
                violationMasterId = 7,
                violationTitle = "Baju keluar tidak dimasukan dengan rapi",
                categoryName = "Kerapian, Seragam & Penampilan",
                points = 10,
                timestamp = now - dayMs * 2,
                notes = "Seragam tidak dimasukkan saat jam istirahat.",
                reporterTeacher = "Ibu Linda (BK)",
                parentNotified = false,
                status = "Tercatat"
            ),

            // Bagus Dwi (Total 110)
            ViolationRecord(
                id = 4,
                studentId = 3,
                studentName = "Bagus Dwi Saputra",
                studentNis = "20261003",
                studentClass = "XI-IPS 2",
                violationMasterId = 20,
                violationTitle = "Bolos, keluar, dan cabut dari jam pelajaran",
                categoryName = "Kedisiplinan & Waktu",
                points = 100,
                timestamp = now - dayMs * 5,
                notes = "Cabut dari kelas saat pelajaran Sosiologi setelah jam istirahat.",
                reporterTeacher = "Bpk. Danang (Guru Mapel Sosiologi)",
                parentNotified = true,
                parentNotificationType = "WhatsApp",
                parentNotificationDate = now - dayMs * 5,
                status = "Selesai"
            ),
            ViolationRecord(
                id = 5,
                studentId = 3,
                studentName = "Bagus Dwi Saputra",
                studentNis = "20261003",
                studentClass = "XI-IPS 2",
                violationMasterId = 1,
                violationTitle = "Terlambat dalam mengikuti proses belajar mengajar",
                categoryName = "Kedisiplinan & Waktu",
                points = 10,
                timestamp = now - dayMs * 1,
                notes = "Datang pukul 07.25 WIB.",
                reporterTeacher = "Bpk. Roni (Satpam/Piket)",
                parentNotified = false,
                status = "Tercatat"
            ),

            // Dimas Arya Wijaya (Total 550)
            ViolationRecord(
                id = 6,
                studentId = 4,
                studentName = "Dimas Arya Wijaya",
                studentNis = "20261004",
                studentClass = "XII-RPL 1",
                violationMasterId = 31,
                violationTitle = "Pemerasan/berkelahi/mengeroyok dan terlibat tawuran sesama siswa/orang lain",
                categoryName = "Pelanggaran Berat, Asusila & Hukum",
                points = 500,
                timestamp = now - dayMs * 1,
                notes = "Terlibat tawuran pelajar di luar pagar sekolah.",
                reporterTeacher = "Bpk. Wahyu (Kesiswaan)",
                parentNotified = true,
                parentNotificationType = "WhatsApp",
                parentNotificationDate = now - dayMs * 1,
                status = "Dalam Pembinaan"
            ),
            ViolationRecord(
                id = 7,
                studentId = 4,
                studentName = "Dimas Arya Wijaya",
                studentNis = "20261004",
                studentClass = "XII-RPL 1",
                violationMasterId = 17,
                violationTitle = "Berambut panjang dan tidak sesuai ketentuan bagi laki-laki",
                categoryName = "Kerapian, Seragam & Penampilan",
                points = 50,
                timestamp = now - dayMs * 4,
                notes = "Rambut bagian belakang melebihi kerah kemeja seragam.",
                reporterTeacher = "Ibu Linda (BK)",
                parentNotified = false,
                status = "Tercatat"
            ),

            // Farah Nabilah (Total 35)
            ViolationRecord(
                id = 8,
                studentId = 5,
                studentName = "Farah Nabilah Putri",
                studentNis = "20261005",
                studentClass = "XI-IPA 2",
                violationMasterId = 12,
                violationTitle = "Surat izin lebih dari tiga kali dengan alasan tidak jelas",
                categoryName = "Kedisiplinan & Waktu",
                points = 25,
                timestamp = now - dayMs * 4,
                notes = "Izin berturut-turut tanpa surat dokter/keterangan jelas.",
                reporterTeacher = "Ibu Sri (Wali Kelas)",
                parentNotified = false,
                status = "Tercatat"
            ),
            ViolationRecord(
                id = 9,
                studentId = 5,
                studentName = "Farah Nabilah Putri",
                studentNis = "20261005",
                studentClass = "XI-IPA 2",
                violationMasterId = 4,
                violationTitle = "Memakai sepatu tidak sesuai peraturan",
                categoryName = "Kerapian, Seragam & Penampilan",
                points = 10,
                timestamp = now - dayMs * 2,
                notes = "Menggunakan sepatu berwarna putih-merah saat hari wajib hitam polos.",
                reporterTeacher = "Ibu Ratna (Piket)",
                parentNotified = false,
                status = "Tercatat"
            ),

            // Hafidz Al-Rasyid (Total 60)
            ViolationRecord(
                id = 10,
                studentId = 7,
                studentName = "Hafidz Al-Rasyid",
                studentNis = "20261007",
                studentClass = "XII-MIPA 3",
                violationMasterId = 16,
                violationTitle = "Memakai anting-anting, kalung, gelang, dan cincin bagi siswa laki-laki",
                categoryName = "Kerapian, Seragam & Penampilan",
                points = 50,
                timestamp = now - dayMs * 8,
                notes = "Memakai gelang tali hitam dan kalung rantai di balik baju.",
                reporterTeacher = "Ibu Linda (BK)",
                parentNotified = false,
                status = "Selesai"
            ),
            ViolationRecord(
                id = 11,
                studentId = 7,
                studentName = "Hafidz Al-Rasyid",
                studentNis = "20261007",
                studentClass = "XII-MIPA 3",
                violationMasterId = 9,
                violationTitle = "Terlambat masuk sesudah istirahat dan pertukaran jam",
                categoryName = "Kedisiplinan & Waktu",
                points = 10,
                timestamp = now - dayMs * 6,
                notes = "Terlambat 10 menit masuk kelas sehabis istirahat siang.",
                reporterTeacher = "Ibu Linda (BK)",
                parentNotified = false,
                status = "Selesai"
            )
        )
    }
}
