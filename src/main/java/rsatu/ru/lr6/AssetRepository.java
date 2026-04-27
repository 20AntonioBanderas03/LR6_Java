package rsatu.ru.lr6;
import java.util.ArrayList;
import java.util.List;

public class AssetRepository<T extends Asset> {
    private List<T> assets = new ArrayList<>();

    public void add(T asset) {
        assets.add(asset);
    }

    public List<T> getAll() {
        return new ArrayList<>(assets);
    }

    public void remove(int index) {
        if (index >= 0 && index < assets.size()) {
            assets.remove(index);
        }
    }

    public int size() {
        return assets.size();
    }

    public T get(int index) {
        return assets.get(index);
    }

    public double getTotalValue() {
        return assets.stream().mapToDouble(Asset::getCurrentValue).sum();
    }

    public List<T> getNonOperational() {
        List<T> result = new ArrayList<>();
        for (T asset : assets) {
            if (!asset.isOperational()) {
                result.add(asset);
            }
        }
        return result;
    }
}