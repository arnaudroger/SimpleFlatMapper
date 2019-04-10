package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.map.annotation.Key;

import java.math.BigDecimal;
import java.util.Collection;

public class Issue626Test {

//IFJAVA8_START
    @Test
    public void test() {

        StringBuilder sql = new StringBuilder ("SELECT products.product_id as id, product_name as name,");
        sql.append ("products.product_description as description,");

        sql.append ("product_price_price as price, photo_id, photo_url as product_photo_url from products");
        sql.append ("INNER JOIN product_prices on products.product_id = product_prices.product_id");

        sql.append (
                "LEFT JOIN (photos INNER JOIN product_photos on photos.photo_id = product_photos.product_photos_photo_id)");
        sql.append (
                "ON products.product_id = product_photos.product_photos_product_id WHERE product_prices.state_id =? ORDER BY products.product_id");



        JdbcMapperBuilder<Product> builder = JdbcMapperFactory.newInstance().newBuilder(Product.class);

        JdbcMapper<Product> mapper = builder.addMapping("id")
                .addMapping("name")
                .addMapping("description")
                .addMapping("price")
                .addMapping("photos_id")
                .addMapping("photos_url")
                .mapper();

    }


    public class Product {
        @Key
        private Long id;

        private String name;

        private String description;

        private BigDecimal price;

        private boolean promotion;

        private java.time.LocalDate created;

        private java.time.LocalDate updated;

        private boolean status;

        private Collection<Photo> photos;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public boolean isPromotion() {
            return promotion;
        }

        public void setPromotion(boolean promotion) {
            this.promotion = promotion;
        }

        public java.time.LocalDate getCreated() {
            return created;
        }

        public void setCreated(java.time.LocalDate created) {
            this.created = created;
        }

        public java.time.LocalDate getUpdated() {
            return updated;
        }

        public void setUpdated(java.time.LocalDate updated) {
            this.updated = updated;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public Collection<Photo> getPhotos() {
            return photos;
        }

        public void setPhotos(Collection<Photo> photos) {
            this.photos = photos;
        }
    }

    public class Photo {
        @Key
        private Long id;

        private String name;

        private String description;

        private String url;
        private java.time.LocalDate created;
        private java.time.LocalDate updated;


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public java.time.LocalDate getCreated() {
            return created;
        }

        public void setCreated(java.time.LocalDate created) {
            this.created = created;
        }

        public java.time.LocalDate getUpdated() {
            return updated;
        }

        public void setUpdated(java.time.LocalDate updated) {
            this.updated = updated;
        }
    }
    //IFJAVA8_END
}
