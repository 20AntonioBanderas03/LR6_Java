package rsatu.ru.lr6;
public class Equipment extends Asset {
    private String modelName;
    private String manufacturer;
    private double purchasePrice;

    public Equipment(String identificationNumber, String modelName, String manufacturer, double purchasePrice) {
        super(identificationNumber);
        this.modelName = modelName;
        this.manufacturer = manufacturer;
        this.purchasePrice = purchasePrice;
    }

    public String getModelName() {
        return modelName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    @Override
    public double getCurrentValue() {
        return purchasePrice * 0.8;
    }

    @Override
    public String getStatusReport() {
        return "Оборудование: " + modelName + " (" + manufacturer + ") - Инв.номер: " + identificationNumber +
                " - Стоимость: " + String.format("%.2f", getCurrentValue()) + " руб. - " + getStatusText();
    }

    public String getStatusText() {
        return isCurrentlyWorking ? "ИСПРАВНО" : "НЕИСПРАВНО";
    }
}