## CPU Zamanlama Algoritmaları Simülasyonu (CPU Scheduling Simulation)

Bu proje, İşletim Sistemleri dersi kapsamında verilen işlem (process) setleri üzerinde farklı **CPU Zamanlama Algoritmalarını** simüle etmek, bu algoritmaların performanslarını analiz etmek ve detaylı raporlar oluşturmak amacıyla **Java** dili ile geliştirilmiştir.

Proje, işletim sistemlerinin çekirdek görevlerinden biri olan işlemci zamanlamasının (Process Scheduling) mantığını kavramak ve FCFS, SJF, Priority, Round Robin gibi temel yaklaşımların verimliliklerini (bekleme süresi, işlem tamamlanma süresi vb.) karşılaştırmak için tasarlanmıştır.

---

##  Öğrenci ve Ders Bilgileri

| Alan | Bilgi |
| :--- | :--- |
| **Öğrenci Adı** | [Berkay Berber] |
| **Öğrenci Numarası** | [20232013087] |
| **Bölüm** | [Bilgisayar Mühendisliği] |
| **Üniversite** | [İstanbul Nişantaşı Üniversitesi] |
| **Ders Kodu & Adı** | [EBLM341 - İşletim Sistemleri] |
| **Ödev Konusu** | [İşlemci Zamanlama Algoritmaları Simülasyonu] |

---

##  Projenin Amacı ve Özellikleri

Bu simülasyon, verilen `.txt` formatındaki veri setlerini okur ve aşağıdaki kısıtlamalara göre işler:
* **Bağlam Değiştirme (Context Switch) Maliyeti:** Her işlem değişiminde `0.001` ms (veya birim zaman) maliyet hesaba katılır.
* **Round Robin Quantum Süresi:** `10` birim zaman olarak belirlenmiştir.
* **Öncelik Seviyeleri:** `High` (1), `Normal` (2), `Low` (3) olarak sayısal değerlere dönüştürülüp işlenir.

###  Simüle Edilen Algoritmalar
Proje, aynı veri seti üzerinde aşağıdaki 6 farklı algoritmayı sırasıyla çalıştırır:

1.  **FCFS (First Come First Served):** İlk gelen işlem ilk yapılır.
2.  **SJF (Shortest Job First) - Non-Preemptive:** En kısa sürecek işlem seçilir (Kesintisiz).
3.  **SJF (Shortest Job First) - Preemptive (SRTF):** Kalan süresi en az olan işlem araya girer (Kesintili).
4.  **Priority Scheduling - Non-Preemptive:** Önceliği yüksek olan işlem seçilir (Kesintisiz).
5.  **Priority Scheduling - Preemptive:** Daha yüksek öncelikli bir iş geldiğinde mevcut iş kesilir (Kesintili).
6.  **Round Robin (RR):** İşlemler belirlenen zaman dilimi (Quantum=10) kadar sırayla çalıştırılır.

---

## 📂 Proje Yapısı

Proje dosyaları aşağıdaki hiyerarşide düzenlenmiştir:

```text 
CPU-Scheduling-Project/
│
├── Odev1.java           # Ana kaynak kod (Main Class ve Algoritmalar)
├── odev1_case1.txt      # Test Veri Seti 1 (Düşük yoğunluklu senaryo)
├── odev1_case2.txt      # Test Veri Seti 2 (Yüksek yoğunluklu senaryo)
├── README.md            # Proje dokümantasyonu
└── sonuclar/            # (Otomatik Oluşur) Çıktı raporlarının kaydedildiği klasör
    ├── odev1_case1_FCFS.txt
    ├── odev1_case1_RoundRobin.txt
    └── ...
```

##  Kurulum ve Çalıştırma
Bu projeyi yerel makinenizde çalıştırmak için bilgisayarınızda Java Development Kit (JDK) kurulu olmalıdır.

1. Projeyi İndirin

Bu repoyu klonlayın veya zip olarak indirip masaüstünde bir klasöre çıkarın. İsterseniz dosyaları kullandığınız IDE'den proje içine atıp'ta direkt çalıştırabilirsiniz.

2. Terminali Açın

Komut satırını (CMD veya Terminal) açın ve proje klasörünün içine girin:

```text 
cd Desktop/ProjeKlasoru
```
3. Derleme (Compile)

Java kodunu derlemek için şu komutu çalıştırın:
```text 
javac Odev1.java
```
4. Çalıştırma (Run)

Derlenen kodu çalıştırmak için:
```text
java Odev1
```
Not: Program çalıştırıldığında odev1_case1.txt ve odev1_case2.txt dosyalarını otomatik olarak arar. Bu dosyaların .java dosyası ile aynı dizinde olduğundan emin olun.

##  Çıktı ve Raporlama
Program çalıştığında sonuclar adında bir klasör oluşturur ve her algoritma için ayrı bir .txt rapor dosyası üretir. Bu raporlar şunları içerir:

a) Zaman Tablosu (Gantt Chart): İşlemlerin hangi zaman aralıklarında çalıştığını gösteren görselleştirilmiş akış.

b) Bekleme Süreleri: Maksimum ve ortalama bekleme süresi.

c) Tamamlanma (Turnaround) Süreleri: Maksimum ve ortalama işlem tamamlanma süresi.

d) Throughput (İş Tamamlama Hızı): T=50, 100, 150 ve 200 anlarında kaç adet işlemin tamamlandığı.

e) CPU Verimliliği: İşlemci kullanım oranı (Bağlam değiştirme maliyetleri düşüldükten sonra).

f) Bağlam Değiştirme Sayısı: Toplam context switch adedi.



