package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

class TopluTasimaPlanlayici {
    // KRİTER AĞIRLIKLARI: Nüfus, Ulaşım Altyapısı, Maliyet, Çevresel Etki, Sosyal Fayda
    // Her kriterin toplam skora katkı oranını belirler. Toplamı 1.0 olmalıdır.
    private static final double[] AGIRLIKLAR = {0.30, 0.20, 0.25, 0.15, 0.10};

    public static void main(String[] args) {
        // 1. MAHALLELERİ OLUŞTUR
        // Sentetik verilerle mahalle listesini hazırla
        List<Mahalle> mahalleler = mahalleleriOlustur();

        // 2. SKORLARI HESAPLA
        // Her mahalleyi kriter ağırlıklarına göre skorla
        List<Double> skorlar = skorlariHesapla(mahalleler);

        // 3. SOFTMAX İLE OLASILIKLARI BELİRLE
        // Skorları 0-1 arası olasılık dağılımına çevir
        List<Double> softmaxOlasiliklari = softmax(skorlar);

        // 4. ANALİZ SONUÇLARINI KONSOLA YAZDIR
        System.out.println("=== Kırklareli Toplu Taşıma Güzergah Analizi ===");
        for(int i=0; i<mahalleler.size(); i++){
            // Formatlı çıktı: Mahalle adı, skoru ve olasılığı
            System.out.printf("%-22s - Skor: %6.2f, Olasılık: %5.2f%%\n",
                    mahalleler.get(i).ad,      // Mahalle adı
                    skorlar.get(i),            // Ağırlıklı skor
                    softmaxOlasiliklari.get(i)*100); // Yüzde cinsinden olasılık
        }

        // 5. DİNAMİK GÜZERGAH OLUŞTUR
        // En yüksek olasılıklı 8 mahalleyi seç
        StringBuilder guzergah = new StringBuilder("\nPlanlanan Güzergah:\n");
        double toplamOlasilik = 0;

        // 5a. MAHALLELERİ OLASILIKLARA GÖRE SIRALA
        // İndeksleri softmax olasılıklarına göre büyükten küçüğe sırala
        List<Integer> siraliIndeksler = IntStream.range(0, mahalleler.size())
                .boxed() // int'i Integer'a çevir
                .sorted(Comparator.comparingDouble(i -> -softmaxOlasiliklari.get(i))) // Ters sıralama
                .limit(8) // İlk 8 mahalleyi al
                .toList(); // Listeye dönüştür

        // 5b. GÜZERGAHA EKLE VE OLASILIKLARI TOPLA
        for(int indeks : siraliIndeksler){
            toplamOlasilik += softmaxOlasiliklari.get(indeks);
            guzergah.append("✓ ")
                    .append(mahalleler.get(indeks).ad)
                    .append(" (")
                    .append(String.format("%.2f%%", softmaxOlasiliklari.get(indeks)*100))
                    .append(")\n");
        }

        // 5c. TOPLAM OLASILIĞI EKLE
        guzergah.append("\nToplam Güzergah Olasılığı: ")
                .append(String.format("%.2f%%", toplamOlasilik*100));

        // 6. GÜZERGAH ÇIKTISINI GÖSTER
        System.out.println(guzergah.toString());
    }

    // MAHALLE VERİLERİNİ OLUŞTURAN METOD
    private static List<Mahalle> mahalleleriOlustur() {
        List<Mahalle> mahalleler = new ArrayList<>();

        // ANA GÜZERGAH DURAKLARI (Yüksek skorlu)
        mahalleler.add(new Mahalle("Kayalı Kampüsü",        98, 95, 20, 90, 95)); // Yüksek nüfus, düşük maliyet
        mahalleler.add(new Mahalle("Ahmet Cevdet Paşa KYK", 92, 88, 25, 85, 90)); // Öğrenci yoğunluğu
        mahalleler.add(new Mahalle("Gençlik Merkezi",       95, 90, 15, 92, 96)); // Sosyal aktivite merkezi
        mahalleler.add(new Mahalle("Pazaryeri",             85, 80, 30, 80, 85)); // Ticari önem
        mahalleler.add(new Mahalle("İl Sağlık Merkezi",     90, 92, 18, 88, 93)); // Kritik tesis
        mahalleler.add(new Mahalle("Vilayet Meydanı",       88, 96, 10, 85, 90)); // Şehir merkezi
        mahalleler.add(new Mahalle("SGK",                  82, 85, 22, 75, 80));  // Kamu binası
        mahalleler.add(new Mahalle("İstasyon Caddesi",      91, 93, 20, 83, 88)); // Ulaşım aksı
        mahalleler.add(new Mahalle("Mini Mall",            87, 89, 28, 78, 82));  // Alışveriş merkezi

        // DİĞER MAHALLELER (Düşük skorlu)
        mahalleler.add(new Mahalle("Hundi Hatun KYK",       40, 50, 85, 30, 45)); // Düşük nüfus
        mahalleler.add(new Mahalle("Green Chef",            35, 40, 90, 20, 35)); // Az talep
        mahalleler.add(new Mahalle("Otogar",               60, 65, 75, 50, 60));  // Çevre yolu
        mahalleler.add(new Mahalle("Akasya",               45, 55, 80, 40, 50));  // Kenar mahalle
        mahalleler.add(new Mahalle("Nergis",               50, 60, 78, 45, 55));  // Orta düzey
        mahalleler.add(new Mahalle("Papatya",              55, 58, 82, 50, 60));  // Düşük öncelik
        mahalleler.add(new Mahalle("Şehir Mezarlığı",      20, 30, 95, 10, 15));  // Minimum etki
        mahalleler.add(new Mahalle("Karahıdır",            30, 40, 88, 25, 35));  // Kırsal bölge

        return mahalleler;
    }

    // SKOR HESAPLAMA METODU
    private static List<Double> skorlariHesapla(List<Mahalle> mahalleler) {
        List<Double> skorlar = new ArrayList<>();
        for(Mahalle m : mahalleler){
            double skor = 0;
            // Nüfus yoğunluğunun katkısı (%30)
            skor += m.nufusYogunlugu * AGIRLIKLAR[0];
            // Ulaşım altyapısının katkısı (%20)
            skor += m.ulasimAltyapisi * AGIRLIKLAR[1];
            // Maliyetin ters katkısı (%25, yüksek maliyet kötü)
            skor += (100 - m.maliyet) * AGIRLIKLAR[2];
            // Çevresel etkinin katkısı (%15)
            skor += m.cevreselEtki * AGIRLIKLAR[3];
            // Sosyal faydanın katkısı (%10)
            skor += m.sosyalFayda * AGIRLIKLAR[4];
            skorlar.add(skor);
        }
        return skorlar;
    }

    // SOFTMAX METODU
    private static List<Double> softmax(List<Double> skorlar) {
        double ustToplam = 0;
        List<Double> exponents = new ArrayList<>();

        // 1. ADIM: Her skorun exponansiyelini al
        for(double s : skorlar){
            double exp = Math.exp(s); // e^s hesapla
            ustToplam += exp;         // Toplamı güncelle
            exponents.add(exp);       // Listeye ekle
        }

        // 2. ADIM: Olasılıkları hesapla
        List<Double> sonuclar = new ArrayList<>();
        for(double exp : exponents){
            // Olasılık = exp(skor) / tüm exp'lerin toplamı
            sonuclar.add(exp / ustToplam);
        }
        return sonuclar;
    }

    // MAHALLE SINIFI: Veri modeli
    static class Mahalle {
        String ad;
        int nufusYogunlugu;
            int ulasimAltyapisi;
            int maliyet;
        int cevreselEtki;
        int sosyalFayda;

        // Constructor: Mahalle nesnesi oluşturucu
        public Mahalle(
                String ad,
                int nufus,
                int altyapi,
                int maliyet,
                int cevre,
                int sosyal
        ) {
            this.ad = ad;
            this.nufusYogunlugu = nufus;
            this.ulasimAltyapisi = altyapi;
            this.maliyet = maliyet;
            this.cevreselEtki = cevre;
            this.sosyalFayda = sosyal;
        }
    }
}