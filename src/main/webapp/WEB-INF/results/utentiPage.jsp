<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
   <head>
      <title>Utenti Presenti</title>

      <%@include file="general.jsp" %>
      <script type="text/javascript" src="${pageContext.request.contextPath}/js/utentiPage/utentiPage.js" defer></script>
   </head>
   <body>
      <%@include file="header.jsp" %>
      <%@include file="adminPanel.jsp" %>

      <main class="flex-container">
         <c:choose>
         <c:when test="${requestScope.utenteList == null}">
            <h1>Non sono presenti altri utenti oltre te!</h1>
         </c:when>
         <c:otherwise>
            <table class="table">
               <thead>
                  <tr>
                     <th>CF</th>
                     <th>Username</th>
                     <th>Nome</th>
                     <th>Cognome</th>
                     <th>Tipo</th>
                     <th>Telefono</th>
                  </tr>
               </thead>
               <c:forEach items="${requestScope.utenteList}" var="utenteItem">
                  <c:if test="${sessionScope.utente.CF != utenteItem.CF}">
                     <tr>
                        <td class="cfUtente" data-label="CF">${utenteItem.CF}</td>
                        <td data-label="Username">${utenteItem.username}</td>
                        <td data-label="Nome">${utenteItem.nome}</td>
                        <td data-label="Cognome">${utenteItem.cognome}</td>
                        <td class="tipoUtente tooltip" data-label="Tipo" id="${utenteItem.CF}">
                           <div>${utenteItem.tipo}</div>
                           <span class="tooltiptext">Click Per Cambiare Tipo Utente</span>
                        </td>
                        <td data-label="Telefono">${utenteItem.telefono}</td>
                     </tr>
                  </c:if>
               </c:forEach>
            </table>
         </c:otherwise>
      </c:choose>
      </main>
      <%@include file="footer.jsp"%>
   </body>
</html>