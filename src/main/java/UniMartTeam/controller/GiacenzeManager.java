package UniMartTeam.controller;

import UniMartTeam.model.Beans.Inventario;
import UniMartTeam.model.Beans.Possiede;
import UniMartTeam.model.Beans.Prodotto;
import UniMartTeam.model.Beans.Utente;
import UniMartTeam.model.DAO.InventarioDAO;
import UniMartTeam.model.DAO.ProdottoDAO;
import UniMartTeam.model.EnumForBeans.TipoUtente;
import UniMartTeam.model.Utils.ConPool;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@WebServlet(name = "GiacenzeManager", value = "/GiacenzeManager/*")
public class GiacenzeManager extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        String path = request.getPathInfo() == null ? "/" : request.getPathInfo().replace("/GiacenzeManager", "");
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");

        if(session != null && utente != null)
        {
            if(utente.getTipo().equals(TipoUtente.Amministratore))
            {
                switch(path){

                }
            }
            else
            {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "L'utente corrente non è autorizzato a visualizzare questa pagina");
            }
        }
        else
            response.sendRedirect(request.getServletContext().getContextPath() + "/Login");

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        String path = request.getPathInfo() == null ? "/" : request.getPathInfo().replace("/GiacenzeManager", "");
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");

        if(session != null && utente != null)
        {
            if(utente.getTipo().equals(TipoUtente.Amministratore))
            {
                switch(path){

                    case "/Modify": {
                        String codiceInventario = request.getParameter("codiceInventario");

                        Inventario i = fillInventario(request, codiceInventario);

                        Enumeration<String> params = request.getParameterNames();
                        int index = 0;
                        while (params.hasMoreElements()) {

                            String paramName = params.nextElement();

                            System.out.println("Parameter Name : " + paramName + ", Value : " + request.getParameter(paramName));

                            List<Possiede> list = i.getPossiedeList();

                            Possiede p = new Possiede();

                            Inventario inventarioDummy = new Inventario();
                            inventarioDummy.setCodiceInventario(Integer.parseInt(request.getParameter("codiceInventario")));
                            p.setInventario(inventarioDummy);

                            Prodotto prodottoDummy = new Prodotto();
                            //3 parametri null a turno, mutex
                            //prodottoDummy.setCodiceIAN(Integer.parseInt(request.getParameter("codiceIAN"+index))); //null
                            prodottoDummy.setNome(request.getParameter("nome"+index));//null
                            p.setProdotto(prodottoDummy);

                            p.setGiacenza(Float.parseFloat(request.getParameter("giacenza"+index)));//null

                            if(!list.get(index).equals(p)) {
                                System.out.println("diverso");
                                System.out.println(list.get(index).toString());
                                System.out.println(p.toString());
                            }
                            index++;
                        }


                    }
                    break;

                    case "/addGiacenze": {

                        Inventario i = null;
                        try {
                            i = InventarioDAO.doRetrieveByCond(InventarioDAO.CODICE_INVENTARIO, "="+request.getParameter("codiceInventario")).get(0);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                        Possiede p = new Possiede();
                        Inventario dummy = new Inventario();

                        dummy.setCodiceInventario(i.getCodiceInventario());
                        p.setInventario(i);
                        try {
                            p.setProdotto(ProdottoDAO.doRetrieveByID(Integer.parseInt(request.getParameter("prodotto"))));
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        p.setGiacenza(Float.parseFloat(request.getParameter("giacenza")));
                        i.addPossiedeList(p);

                        try {
                            InventarioDAO.addProdottoInventario(p);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                    }
                        break;
                    case "/": {
                        if (request.getParameter("codiceInventario&CF") != null) {
                            String[] info = request.getParameter("codiceInventario&CF").split(",");
                            if (info.length == 2) {
                                String codiceInventario = info[0], cfResponsabile = info[1];

                                if (cfResponsabile.equalsIgnoreCase(utente.getCF())) {

                                    Inventario i = fillInventario(request, codiceInventario);

                                    if (i != null) {
                                        request.setAttribute("inventario", i);
                                        request.getRequestDispatcher("/WEB-INF/results/giacenzeManager.jsp").forward(request, response);
                                    } else
                                        //errore
                                        System.out.println("errore");
                                }
                            }
                        }
                    }
                    break;
                }
                response.sendRedirect(request.getContextPath() + "/InventarioManager");
                return;
            }
            else
            {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "L'utente corrente non è autorizzato a visualizzare questa pagina");
            }
        }
        else
            response.sendRedirect(request.getServletContext().getContextPath() + "/Login");

    }

    private Inventario fillInventario(HttpServletRequest request, String codiceInventario){
        Inventario i = null;
        List<Prodotto> prodotti = null;

        try {
            i = InventarioDAO.doRetrieveByCond(InventarioDAO.CODICE_INVENTARIO, "=" + codiceInventario).get(0);//TODO problema in DAO con =
            prodotti = ProdottoDAO.doRetrieveAll();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        ArrayList<Possiede> possiedeArrayList = new ArrayList<>();

        for(Prodotto p : prodotti){

            Possiede possiede = new Possiede();
            possiede.setInventario(i);
            possiede.setProdotto(p);
            try {
                if(!InventarioDAO.getProdottoInventarioStock(possiede))
                    possiede.setGiacenza(0);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            possiedeArrayList.add(possiede);

        }
        i.setPossiedeList(possiedeArrayList);

        return i;
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