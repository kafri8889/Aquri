package com.anafthdev.aquri.data.model.enum

import com.anafthdev.aquri.R

enum class DrinkBottleIcon(val resId: Int) {

    Bottle1(R.drawable.ic_bottle1_24dp),
    Bottle2(R.drawable.ic_bottle2_24dp),
    Bottle3(R.drawable.ic_bottle3_24dp),
    Bottle4(R.drawable.ic_bottle4_24dp),
    Bottle5(R.drawable.ic_bottle5_24dp),
    Bottle6(R.drawable.ic_bottle6_24dp),
    Bottle7(R.drawable.ic_bottle7_24dp),
    Bottle8(R.drawable.ic_bottle8_24dp),
    Bottle9(R.drawable.ic_bottle9_24dp),
    Bottle10(R.drawable.ic_bottle10_24dp),
    Bottle11(R.drawable.ic_bottle11_24dp),
    Bottle12(R.drawable.ic_bottle12_24dp),
    Bottle13(R.drawable.ic_bottle13_24dp),
    Bottle14(R.drawable.ic_bottle14_24dp),

    Cup1(R.drawable.ic_cup1_24dp),
    Cup2(R.drawable.ic_cup2_24dp),
    Cup3(R.drawable.ic_cup3_24dp),
    Cup4(R.drawable.ic_cup4_24dp),
    Cup5(R.drawable.ic_cup5_24dp),

    Glass1(R.drawable.ic_glass1_24dp),
    Glass2(R.drawable.ic_glass2_24dp),

    Gallon(R.drawable.ic_gallon_24dp),
    WaterBottle(R.drawable.ic_water_bottle_24dp),
    WaterBottleLarge(R.drawable.ic_water_bottle_large_24dp),

    Cola(R.drawable.ic_bottle_cola_24dp),
    Soda(R.drawable.ic_bottle_soda_24dp),
    Plastic(R.drawable.ic_bottle_plastic_24dp),
    Milk(R.drawable.ic_milk_bottle_24dp),

    Medicine(R.drawable.ic_medicine_bottle_24dp),
    Liquor(R.drawable.ic_liquor_24dp),
    Propane(R.drawable.ic_propane_tank_24dp),
    Pediatrics(R.drawable.ic_pediatrics_24dp),

    Bottles(R.drawable.ic_bottles_24dp);

    companion object {
        fun fromString(name: String): DrinkBottleIcon {
            return try {
                valueOf(name)
            } catch (e: Exception) {
                Bottle1 // fallback
            }
        }
    }
}
