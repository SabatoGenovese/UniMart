<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>${title}</title>
    <%@include file="general.jsp"%>
    <link href="./css/adminPages.css" type="text/css" rel="stylesheet">
</head>
<body>
    <%@include file="adminPanel.jsp" %>
    <c:set var="index" scope="page" value="0"></c:set>

    <c:choose>
        <c:when test="${inventario.possiedeList == null}">
            <h1>Nessun prodotto creato...</h1>
        </c:when>
        <c:otherwise>
            <form action="GiacenzeManager/Modify" method="post">
                <input type="hidden" name="codiceInventario" id="codiceInventario" value="${inventario.codiceInventario}">
                <table>
                    <tr>
                        <th>Codice IAN</th>
                        <th>Nome</th>
                        <th>Categoria</th>
                        <th>Prezzo</th>
                        <th>Foto</th>
                        <th>Giacenza</th>
                    </tr>
                    <c:forEach items="${inventario.possiedeList}" var="list">
                        <tr>
                            <td class="tdSmall">${list.prodotto.codiceIAN}</td>
                            <input type="hidden" name="codiceIAN${index}" value="${list.prodotto.codiceIAN}">
                            <td class="tdSmall">${list.prodotto.nome}</td>
                            <input type="hidden" name="nome${index}" value="${list.prodotto.nome}">
                            <td class="tdSmall">${list.prodotto.categoria.nome}</td>

                            <td class="tdSmall">${list.prodotto.prezzo}</td>

                            <td class="tdSmall">
                                <c:if test="${not empty list.prodotto.foto}">

                                    <img src="file/${list.prodotto.foto}" height="100" width="100">

                                </c:if>
                            </td>
                            <td class="tdSmall">
                                <input type="number" name="giacenza${index}" value="${list.giacenza}">
                            </td>
                        </tr>
                        <c:set var="index" value="${index+1}"/>
                    </c:forEach>
                </table>
                <button type="submit">Salva modifiche</button>
            </form>
        </c:otherwise>
    </c:choose>



</body>
</html>