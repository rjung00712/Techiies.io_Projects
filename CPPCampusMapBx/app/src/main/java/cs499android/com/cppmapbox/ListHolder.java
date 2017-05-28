package cs499android.com.cppmapbox;

import java.util.ArrayList;

/**
 * Created by awing_000 on 5/27/2017.
 */

public abstract class ListHolder
{
    protected static ArrayList<String> Buildings = new ArrayList<>();
    protected static ArrayList<String> Admin = new ArrayList<>();
    protected static ArrayList<String> Resident = new ArrayList<>();
    protected static ArrayList<String> Food = new ArrayList<>();
    protected static ArrayList<String> Parking = new ArrayList<>();
    protected static ArrayList<String> Landmarks = new ArrayList<>();
    protected static ArrayList<String> Bathrooms = new ArrayList<>();

    protected static void addTo(String category, String name)
    {
        switch (category)
        {
            case "buildings":
                Buildings.add(name);
                break;
            case "food":
                Food.add(name);
                break;
            case "parking":
                Parking.add(name);
                break;
            case "residence":
                Resident.add(name);
                Buildings.add(name);
                break;
            case "landmarks":
                Landmarks.add(name);
                break;
            case "bathrooms":
                Bathrooms.add(name);
                break;
            case "admin":
                Admin.add(name);
                Buildings.add(name);
                break;
            case "bathroom":
                Bathrooms.add(name);
                break;
            default:
                break;
        }
    }
}
