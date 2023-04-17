<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="URL.jspf" %>
<c:set var="trattamento" value="${requestScope.trattamento}" scope="page" />
<fmt:parseDate var="lastModified" value="${trattamento.dataUltimaModifica}" pattern="yyyy-MM-dd" scope="page" />
<fmt:formatDate var="dataUltimaModifica" value="${lastModified}" pattern="dd MMMMM yyyy" />
<c:catch var="exception">
    <div class="container-lg p-3 my-3 bg-primary text-white">
      <h2 align="center">
        <c:out value="${trattamento.nome}" />
      </h2>
      <div class="text-white centerlayout">(<c:out value="${trattamento.codice}" />)</div>
    </div>
    <h3 class="bordo heading pHeader">Descrizione sintetica del trattamento</h3>
    <div class="form-custom">
      <div class="info">
        <c:out value="${trattamento.descrizione}" escapeXml="false" />
      </div>
  <c:if test="${not empty trattamento.attivita}">
      <hr class="separatore" />
      <span class="bordo">Rientrano nel trattamento le seguenti attivit&agrave;:</span>
    <c:set var="listClass" value="" scope="page" />
    <c:if test="${trattamento.attivita.size() lt 7}">
      <c:set var="listClass" value="list-group-horizontal" scope="page" />
    </c:if>
      <ul class="list-group ${listClass}">
    <c:forEach var="attivita" items="${trattamento.attivita}">
        <li class="list-group-item">
          <c:out value="${attivita.nome}" escapeXml="false" />
          (<c:out value="${attivita.codice}" />)
        </li>
    </c:forEach>
      </ul>
  </c:if>
    </div>
    <hr class="separatore" />
    <h3 class="bordo heading pHeader">Art. 30 1b): Descrizione delle Finalit&agrave; perseguite</h3>
    <div class="form-custom">
      <c:out value="${trattamento.finalita}" escapeXml="false" />
    </div>
    <hr class="separatore" />
  <c:if test="${not empty trattamento.informativa}">
    <h4 class="bordo">Paesi Terzi</h4>
    <div class="form-custom">
      <c:out value="${trattamento.informativa}" escapeXml="false" />
    </div>
    <hr class="separatore" />
  </c:if>
  <c:if test="${not empty trattamento.interessati}">
    <h3 class="bordo heading pHeader">Art. 30 1c): Descrizione delle categorie di interessati</h3>
    <div class="form-custom">
      <ul class="list-group">
      <c:forEach var="interessati" items="${trattamento.interessati}">
        <li class="list-group-item">
          <c:out value="${interessati.nome}" escapeXml="false" />
          (<c:out value="${interessati.informativa}" />)
        </li>
      </c:forEach>
      </ul>
    </div>
    <hr class="separatore" />
  </c:if>
    <div>
      <h3 class="bordo heading pHeader">Art. 30 1c): Descrizione delle categorie di dati personali:</h3>            
      <table class="table table-striped">
        <tbody>
          <tr>
            <td>
          <c:choose>
            <c:when test="${trattamento.datiPersonali}">
              <i class="fa-regular fa-square-check bg-success"></i>
            </c:when>
            <c:otherwise>
              <i class="fa-solid fa-circle-xmark alert-danger"></i>
            </c:otherwise>
          </c:choose>
            </td>
            <td>Dati personali</td>
          </tr>
          <tr>
            <td>    
          <c:choose>
            <c:when test="${trattamento.datiSanitari}">
              <i class="fa-regular fa-square-check bg-success"></i>
            </c:when>
            <c:otherwise>
              <i class="fa-solid fa-circle-xmark alert-danger"></i>
            </c:otherwise>
          </c:choose>
            </td>
            <td>Dati sanitari</td>
          </tr>
          <tr>
            <td>
          <c:choose>
            <c:when test="${trattamento.datiOrientamentoSex}">
              <i class="fa-regular fa-square-check bg-success"></i>
            </c:when>
            <c:otherwise>
              <i class="fa-solid fa-circle-xmark alert-danger"></i>
            </c:otherwise>
          </c:choose>
            </td>
            <td>Dati relativi all'orientamento sessuale</td>
          </tr>
          <tr>
            <td>
          <c:choose>
            <c:when test="${trattamento.datiEtniaReligApp}">
              <i class="fa-regular fa-square-check bg-success"></i>
            </c:when>
            <c:otherwise>
              <i class="fa-solid fa-circle-xmark alert-danger"></i>
            </c:otherwise>
          </c:choose>
            </td>
            <td>Dati relativi ad etnia, religione o appartenenza associativa</td>
          </tr>
          <tr>
            <td>
          <c:choose>
            <c:when test="${trattamento.datiMinoreEta}">
              <i class="fa-regular fa-square-check bg-success"></i>
            </c:when>
            <c:otherwise>
              <i class="fa-solid fa-circle-xmark alert-danger"></i>
            </c:otherwise>
          </c:choose>
            </td>
            <td>Dati relativi a soggetti minorenni</td>
          </tr>
          <tr>
            <td>
          <c:choose>
            <c:when test="${trattamento.datiGenetici}">
              <i class="fa-regular fa-square-check bg-success"></i>
            </c:when>
            <c:otherwise>
              <i class="fa-solid fa-circle-xmark alert-danger"></i>
            </c:otherwise>
          </c:choose>
            </td>
            <td>Dati relativi ad aspetti genetici</td>
          </tr>
          <tr>
            <td>
          <c:choose>
            <c:when test="${trattamento.datiBiometrici}">
              <i class="fa-regular fa-square-check bg-success"></i>
            </c:when>
            <c:otherwise>
              <i class="fa-solid fa-circle-xmark alert-danger"></i>
            </c:otherwise>
          </c:choose>
            </td>
            <td>Dati biometrici</td>
          </tr>
          <tr>
            <td>
          <c:choose>
            <c:when test="${trattamento.datiGiudiziari}">
              <i class="fa-regular fa-square-check bg-success"></i>
            </c:when>
            <c:otherwise>
              <i class="fa-solid fa-circle-xmark alert-danger"></i>
            </c:otherwise>
          </c:choose>
            </td>
            <td>Dati giudiziari</td>
          </tr>
          <tr>
            <td>
          <c:choose>
            <c:when test="${trattamento.datiUbicazione}">
              <i class="fa-regular fa-square-check bg-success"></i>
            </c:when>
            <c:otherwise>
              <i class="fa-solid fa-circle-xmark alert-danger"></i>
            </c:otherwise>
          </c:choose>
            </td>
            <td>Dati relativi all'ubicazione dei soggetti</td>
          </tr>
          <tr>
            <td>
          <c:choose>
            <c:when test="${trattamento.datiPseudonimizzati}">
              <i class="fa-regular fa-square-check bg-success"></i>
            </c:when>
            <c:otherwise>
              <i class="fa-solid fa-circle-xmark alert-danger"></i>
            </c:otherwise>
          </c:choose>
            </td>
            <td>Dati pseudonimizzati</td>
          </tr>
          <tr>
            <td>
          <c:choose>
            <c:when test="${trattamento.datiAnonimizzati}">
              <i class="fa-regular fa-square-check bg-success"></i>
            </c:when>
            <c:otherwise>
              <i class="fa-solid fa-circle-xmark alert-danger"></i>
            </c:otherwise>
          </c:choose>
            </td>
            <td>Dati anonimizzati</td>
          </tr>
        </tbody>
      </table>
    </div>
    <hr class="separatore" />
  <c:if test="${not empty trattamento.extraInfos.extraInfo3}">

    <h3 class="bordo heading pHeader">Art. 30 1d): Categorie di destinatari a cui i dati vengono comunicati:</h3>
    <div class="form-custom">
      <ul>
    <c:forTokens var="receiver" items="${trattamento.extraInfos.extraInfo3}" delims="-">
      <li class="list-group-item">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-right-short" viewBox="0 0 16 16">
          <path fill-rule="evenodd" d="M4 8a.5.5 0 0 1 .5-.5h5.793L8.146 5.354a.5.5 0 1 1 .708-.708l3 3a.5.5 0 0 1 0 .708l-3 3a.5.5 0 0 1-.708-.708L10.293 8.5H4.5A.5.5 0 0 1 4 8z"/>
        </svg>
        <c:out value="${receiver}" escapeXml="false" />
      </li>
    </c:forTokens>
      </ul>
    </div>
    <hr class="separatore" />
  </c:if>
    <h3 class="bordo heading pHeader">Art. 30 1f): Termini ultimi previsti per la cancellazione</h3>
    <div class="form-custom">
      <c:out value="${trattamento.terminiUltimi}" escapeXml="false" />
    </div>
    <hr class="separatore" />
  <c:if test="${not empty trattamento.extraInfos.extraInfo1}">
    <h3 class="bordo heading pHeader">Art. 30 1g) Descrizione generale delle misure di sicurezza tecniche ed organizzative</h3>
    <div class="form-custom">
      <c:out value="${trattamento.extraInfos.extraInfo1}" escapeXml="false" />
    </div>
    <hr class="separatore" />
  </c:if>
  <c:if test="${not empty trattamento.extraInfos.extraInfo2}">
    <h3 class="bordo heading pHeader">Luoghi di custodia dei supporti di memorizzazione</h3>
    <div class="form-custom">
      <c:out value="${trattamento.extraInfos.extraInfo2}" escapeXml="false" />
    </div>
    <hr class="separatore" />
  </c:if>
  <c:if test="${not empty trattamento.extraInfo}">
    <h3 class="bordo heading pHeader">Ulteriori informazioni</h3>
    <div class="form-custom">
      <c:out value="${trattamento.extraInfo}" escapeXml="false" />
    </div>
    <hr class="separatore" />
  </c:if>
    <hr class="separatore" />
    <div class="col-12 text-center">
      <a href="${tr}" class="btn btn-primary" title="Torna all'elenco">
        <i class="far fa-times-circle"></i> Chiudi 
      </a>
      <a href="javascript:print()" class="btn btn-warning" title="Anteprima di stampa">
        <i class="fas fa-print"></i> Stampa
       </a> 
    </div>
    
    
</c:catch>
<c:if test="${not empty exception}">
  <div class="alert alert-danger">
    <strong>Spiacente!</strong>
    <p>
      Si &egrave; verificato un problema<br/>
      <c:out value="${exception}" />
    </p>
  </div>
</c:if>
