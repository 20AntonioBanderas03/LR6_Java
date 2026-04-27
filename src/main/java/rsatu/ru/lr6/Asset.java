package rsatu.ru.lr6;
public abstract class Asset<T> implements Maintainable {
    protected T identificationNumber;
    protected boolean isCurrentlyWorking;

    public Asset(T identificationNumber) {
        this.identificationNumber = identificationNumber;
        this.isCurrentlyWorking = true;
    }

    public T getInventoryNumber() {
        return identificationNumber;
    }

    @Override
    public boolean isOperational() {
        return isCurrentlyWorking;
    }

    public void setOperational(boolean isCurrentlyWorking) {
        this.isCurrentlyWorking = isCurrentlyWorking;
    }

    public abstract double getCurrentValue();
}