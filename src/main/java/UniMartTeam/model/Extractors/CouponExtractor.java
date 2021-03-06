package UniMartTeam.model.Extractors;

import UniMartTeam.model.Beans.Coupon;
import UniMartTeam.model.Beans.Ordine;
import UniMartTeam.model.Beans.Utente;
import UniMartTeam.model.EnumForBeans.StatoCoupon;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CouponExtractor
{
   public static Coupon Extract(ResultSet rs, String alias, Utente utente, Ordine ordine) throws SQLException
   {
      if (rs != null)
      {
         Coupon c = new Coupon();

         if (!alias.isEmpty())
            alias += ".";

         c.setNumeroCoupon(rs.getInt("numeroCoupon"));
         c.setStatoCoupon(StatoCoupon.StringToEnum(rs.getString("stato")));
         c.setSconto(rs.getFloat("sconto"));
         c.setCreatore(utente);
         c.setOrdine(ordine);

         return c;
      }
      return null;
   }
}