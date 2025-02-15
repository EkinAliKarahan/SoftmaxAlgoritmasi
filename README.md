# SoftmaxAlgoritmasi

NOT1: Ödevde 3 mahalle arasından 1 tane mi seçmemiz gerektiğini yoksa 3 mahalle arasında rota mı oluşturmamız gerektiğini tam anlayamadım ve onun yerine ve çok sayıda durak arasından uygun bir rota oluşturan bir algoritma oluşturdum

NOT2: Videoda bahsettiğim nedenden ötürü konsol çıktısını gösteremedim ancak kodu yeni dosyada açıp konsol çıktısını ss alarak teams'e yükledim

Softmax Algoritması Nedir?

Softmax algoritması, bir dizi sayıyı (skorları) olasılık dağılımına dönüştüren matematiksel bir fonksiyondur. Bu algoritma, özellikle makine öğrenmesi ve istatistik alanlarında sıkça kullanılır. Softmax'ın temel amacı, bir grup skoru normalize ederek her bir skorun 0 ile 1 arasında bir olasılık değeri almasını ve tüm olasılıkların toplamının 1 olmasını sağlamaktır.
Softmax Formülü:
Softmax(xi)=exi∑j=1nexj
Softmax(xi​)=∑j=1n​exj​exi​​

Burada:

    xixi​: İlgili skor.

    exiexi​: Skorun exponansiyel değeri (e üzeri x_i).

    ∑j=1nexj∑j=1n​exj​: Tüm skorların exponansiyel değerlerinin toplamı.

Projenin Çalışma Mantığı

    Mahalle Verileri:

        Her mahalle için nüfus yoğunluğu, ulaşım altyapısı, maliyet, çevresel etki ve sosyal fayda kriterleri belirlenir.

        Bu kriterler, önceden tanımlanmış ağırlıklarla çarpılarak her mahalle için bir skor hesaplanır.

    Softmax ile Olasılıkların Hesaplanması:

        Skorlar, Softmax algoritması kullanılarak olasılık dağılımına dönüştürülür.

        Bu sayede her mahallenin toplu taşıma güzergahına dahil olma olasılığı belirlenir.

    Güzergahın Belirlenmesi:

        En yüksek olasılığa sahip 8 mahalle güzergaha dahil edilir.

        Güzergah, bu mahallelerin olasılıklarına göre dinamik olarak oluşturulur.

Kodun Yapısı
1. Mahalle Sınıfı (Mahalle):

    Her mahallenin özelliklerini (ad, nüfus yoğunluğu, ulaşım altyapısı, maliyet, çevresel etki, sosyal fayda) saklar.

2. Skor Hesaplama:

    Her mahalle için skor, kriterlerin ağırlıklı toplamı olarak hesaplanır:
    Skor=(Nu¨fus×0.30)+(Altyapı×0.20)+((100−Maliyet)×0.25)+(C¸evresel Etki×0.15)+(Sosyal Fayda×0.10)
    Skor=(Nu¨fus×0.30)+(Altyapı×0.20)+((100−Maliyet)×0.25)+(C¸​evresel Etki×0.15)+(Sosyal Fayda×0.10)

3. Softmax Algoritması:

    Skorlar, exponansiyel fonksiyonla işlenir ve normalize edilir:
    Softmax(xi)=exi∑j=1nexj
    Softmax(xi​)=∑j=1n​exj​exi​​

4. Güzergah Oluşturma:

    En yüksek olasılıklı 8 mahalle seçilir ve güzergah oluşturulur.
