public class IpDemo {
    public static void main(String[] args) throws Exception {
        //city db demo，获取城市
        String basePath = IpDemo.class.getResource("./geoipdb").getPath();
        System.out.println(basePath);
        Geo cityGeo = new Geo(basePath+"/GeoLite2-City.mmdb");
        Geo countryGeo = new Geo(basePath+"/GeoLite2-Country.mmdb");

        String ip1= "211.145.63.25";
        System.out.printf("ip=%s,country=%s,city=%s\n",ip1,countryGeo.getCountryIsoCode(ip1),cityGeo.getCityName(ip1));
        String ip2= "114.114.114.114";
        System.out.printf("ip=%s,country=%s,city=%s\n",ip2,countryGeo.getCountryIsoCode(ip2),cityGeo.getCityName(ip2));
        /**out put
        ip=211.145.63.25,country=CN,city=Beijing
        ip=114.114.114.114,country=CN,city=Nanjing
        */
        //显示城市中文名、英文名
        System.out.printf("ip=%s, city_CN=%s",ip1,cityGeo.getCityNameByLanguageKey(ip1,"zh-CN"));
        System.out.printf("ip=%s, city_CN=%s",ip2,cityGeo.getCityNameByLanguageKey(ip2,"zh-CN"));
    }
}
