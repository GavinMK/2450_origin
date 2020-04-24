package origin.model;

import java.util.Date;
import java.util.Set;

/*
    All the data associated with a single video game
 */
public class GameData {
    public String title;
    public String description;
    public int numSales;
    public Date datePublished;
    public boolean owned;
    public float price;
    public float salesPrice;
    public String largeImgUri;
    public String vertImgUri;
    public String horzImgUri;
    public String gifUri;
    public String color;
    public Set<String> categories;
    public Set<String> filters;
    public String saleGroup;
}
