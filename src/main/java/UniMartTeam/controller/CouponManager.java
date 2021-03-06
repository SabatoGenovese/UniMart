package UniMartTeam.controller;

import UniMartTeam.model.Beans.Coupon;
import UniMartTeam.model.Beans.Utente;
import UniMartTeam.model.DAO.CouponDAO;
import UniMartTeam.model.EnumForBeans.StatoCoupon;
import UniMartTeam.model.EnumForBeans.TipoUtente;
import UniMartTeam.model.Utils.ConPool;
import UniMartTeam.model.Utils.Validator;
import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "CouponManager", value = "/CouponManager/*")
public class CouponManager extends HttpServlet
{
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      String path = request.getPathInfo() == null ? "/" : request.getPathInfo();
      SessionManager sessionManager = new SessionManager(request);
      Utente utente = (Utente) sessionManager.getObjectFromSession("utente");

      if(utente != null)
      {
         if(utente.getTipo().equals(TipoUtente.Amministratore))
         {
            if ("/".equals(path))
            {
               if (sessionManager.getObjectFromSession("ultimoCoupon") != null)
               {
                  request.setAttribute("ultimoCoupon", sessionManager.getObjectFromSession("ultimoCoupon"));
                  sessionManager.removeAttribute("ultimoCoupon");
               }

               listCoupon(request, response);
            }
            else
               response.sendRedirect(request.getServletContext().getContextPath() + getServletContext().getInitParameter("homepage"));
         }
         else
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "L'utente corrente non è autorizzato a visualizzare questa pagina");
      }
      else
         response.sendRedirect(request.getServletContext().getContextPath() + "/LoginManager");
   }

   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      String path = request.getPathInfo() == null ? "/" : request.getPathInfo();
      SessionManager sessionManager = new SessionManager(request);
      Utente utente = (Utente) sessionManager.getObjectFromSession("utente");
      Validator validator = new Validator(request);

      if(utente != null && validator.required(utente.getCF()))
      {
         if ("/validateCoupon".equals(path))
         {
            Coupon coupon = null;

            if (validator.assertInt("ID", "Formato ID non valido"))
            {
               try
               {
                  coupon = CouponDAO.doRetrieveById(Integer.parseInt(request.getParameter("ID")));
               }
               catch (SQLException throwables)
               {
                  throwables.printStackTrace();
               }
            }

            if(coupon != null)
            {
               coupon.setOrdine(null);
               coupon.setCreatore(null);

               if(coupon.getStatoCoupon().equals(StatoCoupon.Riscattato))
                  coupon = null;
            }

            Gson json = new Gson();
            response.setContentType("application/JSON");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(json.toJson(coupon));

            return;
         }

         if(!utente.getTipo().equals(TipoUtente.Amministratore))
         {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "L'utente corrente non è autorizzato a visualizzare questa pagina");
            return;
         }

         switch (path)
         {
            case "/creaCoupon":
            {
               Coupon coupon;

               if (validator.assertDouble("sconto", "Formato Sconto non valido"))
               {
                  coupon = new Coupon();
                  coupon.setSconto(Float.parseFloat(request.getParameter("sconto")));
                  coupon.setCreatore(utente);

                  try
                  {
                     coupon.setNumeroCoupon(CouponDAO.doSave(coupon));
                  } catch (SQLException e)
                  {
                     request.setAttribute("message", "Errore nel salvataggio del coupon nel Database(Servlet:CouponMAnager Metodo:doPost)");
                     request.setAttribute("exceptionStackTrace", e.getStackTrace());
                     response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
                     return;
                  }

                  sessionManager.setAttribute(coupon, "ultimoCoupon");
               }
            }
            break;

            case "/deleteCoupon":
            {
               if (checkParam(validator) && request.getParameter("CF_Creatore").equalsIgnoreCase(utente.getCF()))
               {
                  int idCoupon = Integer.parseInt(request.getParameter("idCoupon"));
                  if (checkStatus(idCoupon))
                  {
                     try
                     {
                        CouponDAO.doDelete(idCoupon);
                     }
                     catch (SQLException e)
                     {
                        request.setAttribute("exceptionStackTrace", e.getMessage());
                        request.setAttribute("message", "Errore nel eliminazione del coupon dal Database(Servlet:CouponManager Metodo:doPost)");
                        request.getRequestDispatcher("/WEB-INF/results/errorPage.jsp").forward(request, response);
                     }
                  }
               }
            }
            break;

            case "/updateCoupon":
            {
               if (checkParam(validator) && request.getParameter("CF_Creatore").equalsIgnoreCase(utente.getCF()))
               {
                  Coupon coupon = new Coupon();

                  coupon.setNumeroCoupon(Integer.parseInt(request.getParameter("idCoupon")));

                  if (checkStatus(coupon.getNumeroCoupon()))
                  {
                     coupon.setSconto(Float.parseFloat(request.getParameter("sconto")));
                     coupon.setCreatore(utente);

                     try
                     {
                        CouponDAO.doUpdate(coupon);
                     } catch (SQLException e)
                     {
                        request.setAttribute("exceptionStackTrace", e.getMessage());
                        request.setAttribute("message", "Errore nel update del coupon nel Database(Servlet:CouponManager Metodo:doPost)");
                        request.getRequestDispatcher("/WEB-INF/results/errorPage.jsp").forward(request, response);
                     }
                  }
               }
            }
            break;
         }
         response.sendRedirect(request.getContextPath() + "/CouponManager");
         return;
      }

      response.sendRedirect(request.getServletContext().getContextPath() + getServletContext().getInitParameter("homepage"));
   }



   private boolean checkParam(Validator validator)
   {
      return validator.assertCF("CF_Creatore", "Formato CF non valido") &&
              validator.assertDouble("sconto", "Formato Sconto non corretto") && validator.assertInt("idCoupon", "Formato Id coupon non valido");
   }

   private boolean checkStatus(int id)
   {
      Coupon coupon;

      try
      {
         coupon = CouponDAO.doRetrieveById(id);
      }
      catch (SQLException e)
      {
         return false;
      }

      if(coupon != null)
         return !coupon.getStatoCoupon().equals(StatoCoupon.Riscattato);

      return false;
   }

   private void listCoupon(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      List<Coupon> couponList = null;

      try
      {
         couponList = CouponDAO.doRetrieveAll();
      }
      catch (SQLException e)
      {
         e.printStackTrace();
         request.setAttribute("exceptionStackTrace", e.getMessage());
         request.setAttribute("message", "Errore nel recupero info dal Database(Servlet:CouponManager Metodo:listCoupon)");
         request.getRequestDispatcher("/WEB-INF/results/errorPage.jsp").forward(request, response);
      }

      if(couponList != null && couponList.get(0) != null)
         request.setAttribute("couponList", couponList);
      else
         request.setAttribute("couponList", null);


      request.getRequestDispatcher("/WEB-INF/results/couponPage.jsp").forward(request, response);
   }

   @Override
   public void destroy()
   {
      try
      {
         ConPool.deleteConnection();
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      finally
      {
         super.destroy();
      }
   }
}