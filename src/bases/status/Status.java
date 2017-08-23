package bases.status;

public class Status {
    private static final float POWERBASE = 5;
    private static final float HEALTHBASE = 100;
    private float health, mana, damage, power;

    public void setPlayerStatus(float health, float mana, float damage, float power) {
        this.health = health;
        this.mana = mana;
        this.damage = damage;
        this.power = power;
    }

    public Status(float health, float damage){
        this.health = health;
        this.damage = damage;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getMana() {
        return mana;
    }

    public void setMana(float mana) {
        this.mana = mana;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
        if (this.power > POWERBASE) this.power = POWERBASE;
    }
}
