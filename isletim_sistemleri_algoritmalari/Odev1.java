// BERKAY BERBER 20232013087 


import java.io.*;
import java.util.*;

public class Odev1 {

 
    static final double CONTEXT_SWITCH_SURESI = 0.001;
    static final int TIME_QUANTUM = 10; 

    static class Islem {
        String id;
        int gelisZamani;
        int burstZamani;
        int kalanSure;
        int oncelik; // 1: High, 2: Normal, 3: Low


        int baslangic = -1;
        int bitis = 0;
        int bekleme = 0;
        int donus = 0;

        public Islem(String id, int gelis, int burst, String oncelikStr) {
            this.id = id;
            this.gelisZamani = gelis;
            this.burstZamani = burst;
            this.kalanSure = burst;
            
            if (oncelikStr.trim().equalsIgnoreCase("high")) {
                this.oncelik = 1;
            } else if (oncelikStr.trim().equalsIgnoreCase("normal")) {
                this.oncelik = 2;
            } else {
                this.oncelik = 3; 
            }
        }
    }

    public static ArrayList<Islem> dosyaOku(String dosyaAdi) {
        ArrayList<Islem> liste = new ArrayList<>();
        File dosya = new File(dosyaAdi);

        if (!dosya.exists()) {
            System.out.println("HATA: '" + dosyaAdi + "' dosyasi bulunamadi!");
            return null; 
        }

        try {
            Scanner scanner = new Scanner(dosya);
            if(scanner.hasNextLine()) scanner.nextLine(); // Başlığı atla

            while (scanner.hasNextLine()) {
                String satir = scanner.nextLine();
                String[] veri = satir.split(",");
                if (veri.length == 4) {
                    Islem yeni = new Islem(veri[0], 
                                           Integer.parseInt(veri[1]), 
                                           Integer.parseInt(veri[2]), 
                                           veri[3]);
                    liste.add(yeni);
                }
            }
            scanner.close();
        } catch (Exception e) {
            System.out.println("Okuma hatasi: " + e.getMessage());
            return null;
        }
        return liste;
    }

    public static ArrayList<Islem> kopyaOlustur(ArrayList<Islem> orjinal) {
        ArrayList<Islem> kopya = new ArrayList<>();
        for (Islem i : orjinal) {
            Islem yeni = new Islem(i.id, i.gelisZamani, i.burstZamani, "dummy");
            yeni.oncelik = i.oncelik;
            kopya.add(yeni);
        }
        return kopya;
    }

 
    public static void raporYaz(String dosyaAdi, String algoritma, ArrayList<Islem> bitenler, String gantt, int degisimSayisi) {
        
        File klasor = new File("sonuclar");

        if (!klasor.exists()) {
            klasor.mkdir();
        }

        String safDosyaAdi = new File(dosyaAdi).getName(); 
        String cikisIsmi = safDosyaAdi.replace(".txt", "") + "_" + algoritma + ".txt";
        
        File cikisDosyasi = new File(klasor, cikisIsmi);
        
        try {
            BufferedWriter yazar = new BufferedWriter(new FileWriter(cikisDosyasi));
            
            yazar.write("--- " + algoritma + " SONUCLARI ---\n\n");
            yazar.write("a) ZAMAN TABLOSU:\n" + gantt + "\n\n");

            double toplamBekleme = 0;
            double toplamDonus = 0;
            double toplamBurst = 0;
            int maxBekleme = 0;
            int maxDonus = 0;

            int sayac50 = 0, sayac100 = 0, sayac150 = 0, sayac200 = 0;

            for (Islem i : bitenler) {
                toplamBekleme += i.bekleme;
                toplamDonus += i.donus;
                toplamBurst += i.burstZamani;

                if (i.bekleme > maxBekleme) maxBekleme = i.bekleme;
                if (i.donus > maxDonus) maxDonus = i.donus;

                if (i.bitis <= 50) sayac50++;
                if (i.bitis <= 100) sayac100++;
                if (i.bitis <= 150) sayac150++;
                if (i.bitis <= 200) sayac200++;
            }

            double ortBekleme = bitenler.size() > 0 ? toplamBekleme / bitenler.size() : 0;
            double ortDonus = bitenler.size() > 0 ? toplamDonus / bitenler.size() : 0;
            
            double baglamSuresi = degisimSayisi * CONTEXT_SWITCH_SURESI;
            double verimlilik = toplamBurst / (toplamBurst + baglamSuresi);

            yazar.write("b) Bekleme Suresi -> Maks: " + maxBekleme + ", Ortalama: " + String.format("%.2f", ortBekleme) + "\n");
            yazar.write("c) Donus Suresi -> Maks: " + maxDonus + ", Ortalama: " + String.format("%.2f", ortDonus) + "\n");
            
            yazar.write("d) Is Tamamlama Sayisi (Throughput):\n");
            yazar.write("   T=50 : " + sayac50 + "\n");
            yazar.write("   T=100: " + sayac100 + "\n");
            yazar.write("   T=150: " + sayac150 + "\n");
            yazar.write("   T=200: " + sayac200 + "\n");

            yazar.write("e) CPU Verimliligi: %" + String.format("%.2f", verimlilik * 100) + "\n");
            yazar.write("f) Toplam Baglam Degistirme: " + degisimSayisi + "\n");

            yazar.close();
            System.out.println("-> " + cikisDosyasi.getPath() + " olusturuldu."); 
        } catch (IOException e) {
            System.out.println("Yazma hatasi: " + e.getMessage());
        }
    }

    public static void fcfsCalistir(ArrayList<Islem> liste, String dosyaAdi) {
        ArrayList<Islem> islemListesi = kopyaOlustur(liste);
        Collections.sort(islemListesi, new Comparator<Islem>() {
            public int compare(Islem i1, Islem i2) {
                return i1.gelisZamani - i2.gelisZamani;
            }
        });

        int zaman = 0;
        String gantt = "";
        int degisim = 0;
        ArrayList<Islem> bitenler = new ArrayList<>();

        for (Islem i : islemListesi) {
            if (zaman < i.gelisZamani) {
                gantt += "[" + zaman + "]--IDLE--";
                zaman = i.gelisZamani;
            }
            i.baslangic = zaman;
            gantt += "[" + zaman + "]--" + i.id + "--";
            zaman += i.burstZamani;
            gantt += "[" + zaman + "]";
            
            i.bitis = zaman;
            i.donus = i.bitis - i.gelisZamani;
            i.bekleme = i.donus - i.burstZamani;
            bitenler.add(i);
            degisim++;
        }
        raporYaz(dosyaAdi, "FCFS", bitenler, gantt, degisim);
    }

    public static void sjfNonPreemptive(ArrayList<Islem> liste, String dosyaAdi) {
        ArrayList<Islem> havuz = kopyaOlustur(liste);
        Collections.sort(havuz, new Comparator<Islem>() {
            public int compare(Islem i1, Islem i2) { return i1.gelisZamani - i2.gelisZamani; }
        });

        int zaman = 0;
        String gantt = "";
        int degisim = 0;
        ArrayList<Islem> bitenler = new ArrayList<>();
        ArrayList<Islem> hazirKuyruk = new ArrayList<>();

        while (bitenler.size() < havuz.size()) {
            for (Islem i : havuz) {
                if (i.gelisZamani <= zaman && !bitenler.contains(i) && !hazirKuyruk.contains(i)) hazirKuyruk.add(i);
            }

            if (hazirKuyruk.isEmpty()) {
                gantt += "[" + zaman + "]--IDLE--"; zaman++; gantt += "[" + zaman + "]"; continue;
            }

            Collections.sort(hazirKuyruk, new Comparator<Islem>() {
                public int compare(Islem i1, Islem i2) { return i1.burstZamani - i2.burstZamani; }
            });

            Islem secilen = hazirKuyruk.get(0);
            hazirKuyruk.remove(0);

            gantt += "[" + zaman + "]--" + secilen.id + "--";
            zaman += secilen.burstZamani;
            gantt += "[" + zaman + "]";

            secilen.bitis = zaman;
            secilen.donus = secilen.bitis - secilen.gelisZamani;
            secilen.bekleme = secilen.donus - secilen.burstZamani;
            bitenler.add(secilen);
            degisim++;
        }
        raporYaz(dosyaAdi, "SJF_NonPreemptive", bitenler, gantt, degisim);
    }

    public static void sjfPreemptive(ArrayList<Islem> liste, String dosyaAdi) {
        ArrayList<Islem> havuz = kopyaOlustur(liste);
        int zaman = 0;
        String gantt = "";
        int degisim = 0;
        ArrayList<Islem> bitenler = new ArrayList<>();
        Islem aktif = null;

        while (bitenler.size() < havuz.size()) {
            ArrayList<Islem> adaylar = new ArrayList<>();
            for (Islem i : havuz) {
                if (i.gelisZamani <= zaman && i.kalanSure > 0) adaylar.add(i);
            }

            if (adaylar.isEmpty()) {
                if (aktif != null) aktif = null;
                gantt += "[" + zaman + "]--IDLE--"; zaman++; gantt += "[" + zaman + "]"; continue;
            }

            Collections.sort(adaylar, new Comparator<Islem>() {
                public int compare(Islem i1, Islem i2) { return i1.kalanSure - i2.kalanSure; }
            });

            Islem enKisa = adaylar.get(0);
            if (aktif != enKisa) {
                degisim++;
                aktif = enKisa;
                gantt += "[" + zaman + "]--" + aktif.id + "--";
            }
            aktif.kalanSure--;
            zaman++;

            if (aktif.kalanSure == 0) {
                gantt += "[" + zaman + "]";
                aktif.bitis = zaman;
                aktif.donus = aktif.bitis - aktif.gelisZamani;
                aktif.bekleme = aktif.donus - aktif.burstZamani;
                bitenler.add(aktif);
                aktif = null;
            }
        }
        raporYaz(dosyaAdi, "SJF_Preemptive", bitenler, gantt, degisim);
    }

    public static void priorityNonPreemptive(ArrayList<Islem> liste, String dosyaAdi) {
        ArrayList<Islem> havuz = kopyaOlustur(liste);
        int zaman = 0;
        String gantt = "";
        int degisim = 0;
        ArrayList<Islem> bitenler = new ArrayList<>();
        ArrayList<Islem> hazirKuyruk = new ArrayList<>();

        while (bitenler.size() < havuz.size()) {
            for (Islem i : havuz) {
                if (i.gelisZamani <= zaman && !bitenler.contains(i) && !hazirKuyruk.contains(i)) hazirKuyruk.add(i);
            }
            if (hazirKuyruk.isEmpty()) {
                gantt += "[" + zaman + "]--IDLE--"; zaman++; gantt += "[" + zaman + "]"; continue;
            }
            Collections.sort(hazirKuyruk, new Comparator<Islem>() {
                public int compare(Islem i1, Islem i2) {
                    if(i1.oncelik != i2.oncelik) return i1.oncelik - i2.oncelik;
                    return i1.gelisZamani - i2.gelisZamani;
                }
            });
            Islem secilen = hazirKuyruk.get(0);
            hazirKuyruk.remove(0);
            
            gantt += "[" + zaman + "]--" + secilen.id + "--";
            zaman += secilen.burstZamani;
            gantt += "[" + zaman + "]";
            
            secilen.bitis = zaman;
            secilen.donus = secilen.bitis - secilen.gelisZamani;
            secilen.bekleme = secilen.donus - secilen.burstZamani;
            bitenler.add(secilen);
            degisim++;
        }
        raporYaz(dosyaAdi, "Priority_NonPreemptive", bitenler, gantt, degisim);
    }

    public static void priorityPreemptive(ArrayList<Islem> liste, String dosyaAdi) {
        ArrayList<Islem> havuz = kopyaOlustur(liste);
        int zaman = 0;
        String gantt = "";
        int degisim = 0;
        ArrayList<Islem> bitenler = new ArrayList<>();
        Islem aktif = null;

        while (bitenler.size() < havuz.size()) {
            ArrayList<Islem> adaylar = new ArrayList<>();
            for (Islem i : havuz) {
                if (i.gelisZamani <= zaman && i.kalanSure > 0) adaylar.add(i);
            }
            if (adaylar.isEmpty()) {
                if (aktif != null) aktif = null;
                gantt += "[" + zaman + "]--IDLE--"; zaman++; gantt += "[" + zaman + "]"; continue;
            }
            Collections.sort(adaylar, new Comparator<Islem>() {
                public int compare(Islem i1, Islem i2) {
                    if(i1.oncelik != i2.oncelik) return i1.oncelik - i2.oncelik;
                    return i1.gelisZamani - i2.gelisZamani;
                }
            });
            Islem enOncelikli = adaylar.get(0);
            if (aktif != enOncelikli) {
                degisim++;
                aktif = enOncelikli;
                gantt += "[" + zaman + "]--" + aktif.id + "--";
            }
            aktif.kalanSure--;
            zaman++;
            if (aktif.kalanSure == 0) {
                gantt += "[" + zaman + "]";
                aktif.bitis = zaman;
                aktif.donus = aktif.bitis - aktif.gelisZamani;
                aktif.bekleme = aktif.donus - aktif.burstZamani;
                bitenler.add(aktif);
                aktif = null;
            }
        }
        raporYaz(dosyaAdi, "Priority_Preemptive", bitenler, gantt, degisim);
    }

    public static void roundRobin(ArrayList<Islem> liste, String dosyaAdi) {
        ArrayList<Islem> havuz = kopyaOlustur(liste);
        Collections.sort(havuz, new Comparator<Islem>() {
            public int compare(Islem i1, Islem i2) { return i1.gelisZamani - i2.gelisZamani; }
        });

        int zaman = 0;
        String gantt = "";
        int degisim = 0;
        ArrayList<Islem> bitenler = new ArrayList<>();
        ArrayList<Islem> kuyruk = new ArrayList<>();
        int eklenenSayisi = 0;

        while (eklenenSayisi < havuz.size() && havuz.get(eklenenSayisi).gelisZamani <= zaman) {
            kuyruk.add(havuz.get(eklenenSayisi));
            eklenenSayisi++;
        }

        while (bitenler.size() < havuz.size()) {
            if (kuyruk.isEmpty()) {
                gantt += "[" + zaman + "]--IDLE--"; zaman++; gantt += "[" + zaman + "]";
                while (eklenenSayisi < havuz.size() && havuz.get(eklenenSayisi).gelisZamani <= zaman) {
                    kuyruk.add(havuz.get(eklenenSayisi)); eklenenSayisi++;
                }
                continue;
            }
            Islem p = kuyruk.get(0);
            kuyruk.remove(0);
            degisim++;
            
            int calisma = Math.min(TIME_QUANTUM, p.kalanSure);
            gantt += "[" + zaman + "]--" + p.id + "--";
            zaman += calisma;
            gantt += "[" + zaman + "]";
            p.kalanSure -= calisma;

            while (eklenenSayisi < havuz.size() && havuz.get(eklenenSayisi).gelisZamani <= zaman) {
                kuyruk.add(havuz.get(eklenenSayisi)); eklenenSayisi++;
            }

            if (p.kalanSure > 0) {
                kuyruk.add(p);
            } else {
                p.bitis = zaman;
                p.donus = p.bitis - p.gelisZamani;
                p.bekleme = p.donus - p.burstZamani;
                bitenler.add(p);
            }
        }
        raporYaz(dosyaAdi, "RoundRobin", bitenler, gantt, degisim);
    }

    public static void main(String[] args) {
        String[] dosyalar = {"odev1_case1.txt", "odev1_case2.txt"};
        
        System.out.println("Odev Simulasyonu Basliyor...");

        for (String dosyaAdi : dosyalar) {
            System.out.println("\nDosya Araniyor: " + dosyaAdi);
            ArrayList<Islem> liste = dosyaOku(dosyaAdi);

            if (liste == null) {
                System.out.println("ISLEM DURDURULDU: " + dosyaAdi + " eksik oldugu icin devam edilemiyor.");
                continue; 
            }

            System.out.println("Dosya bulundu, algoritmalar calistiriliyor ve 'sonuclar' klasorune kaydediliyor...");
            fcfsCalistir(liste, dosyaAdi);
            sjfNonPreemptive(liste, dosyaAdi);
            sjfPreemptive(liste, dosyaAdi);
            roundRobin(liste, dosyaAdi);
            priorityNonPreemptive(liste, dosyaAdi);
            priorityPreemptive(liste, dosyaAdi);
        }
        System.out.println("\nTum islemler tamamlandi. 'sonuclar' klasorunu kontrol edin.");
    }
}