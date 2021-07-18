<!DOCTYPE html>
<html lang="it">
   <head>
      <title>Unimart</title>

      <%@include file="WEB-INF/results/general.jsp"%>
   </head>
   <body>
      <%@include file="WEB-INF/results/header.jsp"%>
      <main class="footer-present">

         <hr>

         <div class="flex-container flex-dirRow col-gap cover-container">
            <img src="${context}/images/cover.jpg" class="flex-item-40">
            <div class="flex-item-50">
               Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean scelerisque dui eget mattis blandit. Cras
               pellentesque vehicula dui, nec elementum felis suscipit eu. Proin ipsum massa, interdum sit amet rutrum sed,
               porttitor vel tortor. Nunc molestie pharetra justo ut fermentum. In pharetra tempus eros, sed facilisis libero
               hendrerit sed. Sed dictum magna sed metus blandit, at sodales ipsum finibus. In sollicitudin non leo et eleifend.
               Donec dignissim, lorem vitae varius cursus, sem orci bibendum metus, vel placerat enim tortor non lectus.
               Duis auctor nunc commodo quam molestie, eget consequat risus pharetra. Cras pellentesque urna sed diam porttitor,
               sed tempor nibh.
            </div>
            <div class="flex-item-50">
               Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean scelerisque dui eget mattis blandit. Cras
               pellentesque vehicula dui, nec elementum felis suscipit eu. Proin ipsum massa, interdum sit amet rutrum sed,
               porttitor vel tortor. Nunc molestie pharetra justo ut fermentum. In pharetra tempus eros, sed facilisis libero
               hendrerit sed. Sed dictum magna sed metus blandit, at sodales ipsum finibus. In sollicitudin non leo et eleifend.
               Donec dignissim, lorem vitae varius cursus, sem orci bibendum metus, vel placerat enim tortor non lectus.
               Duis auctor nunc commodo quam molestie, eget consequat risus pharetra. Cras pellentesque urna sed diam porttitor,
               sed tempor nibh.
            </div>

            <img src="${context}/images/cover2.jpg" class="flex-item-40">
         </div>

         <hr>

         <c:if test="${categorie != null}">
            <div class="flex-container justify-content-center col-gap categorie-container">
               <c:forEach items="${categorie}" var="categoria">
                  <div class="flex-item-20 transition">
                     <a href="${context}/SearchManager/categoria?id=${categoria.nome}">
                        ${categoria.nome}
                     </a>
                  </div>
               </c:forEach>
            </div>
         </c:if>

         <hr>

      </main>
      <%@include file="WEB-INF/results/footer.jsp"%>
   </body>
</html>