package UniMartTeam.model;

public class Coupon
{
   private int numeroCoupon;
   private StatoCoupon statoCoupon;
   private float sconto;
   private Utente creatore;
   private Ordine ordine;

   public int getNumeroCoupon()
   {
      return numeroCoupon;
   }

   public void setNumeroCoupon(int numeroCoupon)
   {
      this.numeroCoupon = numeroCoupon;
   }

   public StatoCoupon getStatoCoupon()
   {
      return statoCoupon;
   }

   public void setStatoCoupon(StatoCoupon statoCoupon)
   {
      this.statoCoupon = statoCoupon;
   }

   public float getSconto()
   {
      return sconto;
   }

   public void setSconto(float sconto)
   {
      this.sconto = sconto;
   }

   public Utente getCreatore()
   {
      return creatore;
   }

   public void setCreatore(Utente creatore)
   {
      this.creatore = creatore;
   }

   public void setOrdine(Ordine ordine)
   {
      this.ordine = ordine;
   }

   public Ordine getOrdine()
   {
      return ordine;
   }
}