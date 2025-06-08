package me.rexe0.bettersurvival.farming.alcohol.customers;

public abstract class AmountRequest extends Request {
    private final int amount;

    public AmountRequest(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
