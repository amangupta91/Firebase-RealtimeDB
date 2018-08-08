package watersupplier.main.Pojo;

/**
 * Created by Aman Gupta on 19/1/17.
 */

public class First_Menu_POJO {

    public int imageId;
    public String MenuName;

    public First_Menu_POJO(){

    }
    public First_Menu_POJO(int imageId,String MenuName)
    {
        this.imageId=imageId;
        this.MenuName=MenuName;
    }
    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {

        this.imageId = imageId;
    }
    public String getMenuName() {
        return MenuName;
    }
    public void setMenuName(String menuName) {
        MenuName = menuName;
    }
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return MenuName;
    }
}
