package UniMartTeam.controller;

import UniMartTeam.model.Beans.Categoria;
import UniMartTeam.model.Beans.Prodotto;
import UniMartTeam.model.DAO.CategoriaDAO;
import UniMartTeam.model.DAO.InventarioDAO;
import UniMartTeam.model.DAO.ProdottoDAO;
import UniMartTeam.model.Utils.ConPool;
import UniMartTeam.model.Utils.Validator;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@WebServlet(name= "SearchManager", value = "/SearchManager/*", loadOnStartup = 0)
public class SearchManager extends HttpServlet
{
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      String path = request.getPathInfo() == null ? "/" : request.getPathInfo().replace("/SearchManager", "");
      Validator validator = new Validator(request);
      
      switch (path)
      {
         case "/":
            String text = request.getParameter("text");
            HashMap<String, Object> results = new HashMap<>();

            if(validator.required(text))
               text = text.trim();

            try
            {
               List<Categoria> resultsCategoria = CategoriaDAO.doRetrieveByKey(text+"%", true, true, 0, 3);

               if(resultsCategoria != null && resultsCategoria.get(0) != null)
               {
                  results.put("categorie", new ArrayList<>());

                  for(Categoria c : resultsCategoria)
                     ((ArrayList<String>) results.get("categorie")).add(c.getNome());
               }

               List<Prodotto> resultsProdotto = ProdottoDAO.doRetrieveByCondLimit("nome LIKE('" + text + "%')", 0, 3);

               if(resultsProdotto != null && resultsProdotto.get(0) != null)
               {
                  results.put("prodotti", new ArrayList<>());

                  for(Prodotto p : resultsProdotto)
                     ((ArrayList<String>) results.get("prodotti")).add(p.getNome() + " : " + p.getCodiceIAN());
               }

               resultsProdotto = null;
               resultsProdotto = ProdottoDAO.doRetrieveByCondLimit("codiceIAN LIKE('" + text + "%')", 0, 3);

               if(resultsProdotto != null && resultsProdotto.get(0) != null)
               {
                  if(results.get("prodotti") == null)
                     results.put("prodotti", new ArrayList<>());

                  for(Prodotto p : resultsProdotto)
                     ((ArrayList<String>) results.get("prodotti")).add(p.getNome() + " : " + p.getCodiceIAN());
               }
            }
            catch (SQLException e)
            {
               request.setAttribute("exceptionStackTrace", e.getStackTrace());
               request.setAttribute("message", "Errore nel recupero info dal Database(Servlet:SearchManager Metodo:/)");
               request.getRequestDispatcher("/WEB-INF/results/errorPage.jsp").forward(request, response);
            }

            if(results.isEmpty())
               results.put("default", "Nessun Risultato...");

            Gson json = new Gson();
            response.setContentType("application/JSON");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(json.toJson(results));

            break;

         case "/prodotto":

            int codiceIAN = 0;

            if(!validator.assertInt("id", "Id sbagliato"))
               return;

            codiceIAN = Integer.parseInt(request.getParameter("id"));

            Prodotto prodotto = null;
            float quantitaDisponibile = 0;

            try
            {
               prodotto = ProdottoDAO.doRetrieveByID(codiceIAN);

               quantitaDisponibile = InventarioDAO.quantitaTotaleProdottoInventario(prodotto);
            }
            catch (SQLException e)
            {
               request.setAttribute("exceptionStackTrace", e.getStackTrace());
               request.setAttribute("message", "Errore nel recupero info dal Database(Servlet:SearchManager Metodo:/prodotto)");
               request.getRequestDispatcher("/WEB-INF/results/errorPage.jsp").forward(request, response);
            }

            if (prodotto != null)
            {
               request.setAttribute("prodotto", prodotto);
               request.setAttribute("quantitaDisponibile", quantitaDisponibile);
               request.getRequestDispatcher("/WEB-INF/results/schedaProdotto.jsp").forward(request, response);
               return;
            }

            response.sendRedirect(request.getServletContext().getContextPath() + getServletContext().getInitParameter("homepage"));
            break;

         case "/categoria":

            String categoria = request.getParameter("id");

            if(validator.required(categoria))
            {
               try
               {
                  Categoria cat = new Categoria();
                  cat.setNome(categoria);

                  List<Prodotto> prodotti = CategoriaDAO.doRetrieveProducts(cat);

                  request.setAttribute("prodotti", prodotti);
                  request.setAttribute("categoria", cat.getNome());
                  request.getRequestDispatcher("/WEB-INF/results/risultatiRicerca.jsp").forward(request, response);
                  return;
               }
               catch (SQLException e)
               {
                  request.setAttribute("exceptionStackTrace", e.getStackTrace());
                  request.setAttribute("message", "Errore nel recupero info dal Database(Servlet:SearchManager Metodo:/prodotto)");
                  request.getRequestDispatcher("/WEB-INF/results/errorPage.jsp").forward(request, response);
               }
            }

            break;

         default:
         {
            response.sendRedirect(request.getServletContext().getContextPath() + getServletContext().getInitParameter("homepage"));
            return;
         }
      }
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