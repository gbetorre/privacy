<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="trattamento" value="${requestScope.trattamento}" scope="page" />
<fmt:parseDate var="lastModified" value="${trattamento.dataUltimaModifica}" pattern="yyyy-MM-dd" scope="page" />
<fmt:formatDate var="dataUltimaModifica" value="${lastModified}" pattern="dd MMMMM yyyy" />
<c:catch var="exception">
    <div class="p-3 my-3 bg-primary text-white">
      <h2 align="center">
        <c:out value="${trattamento.nome}" />
      </h2>
      <div class="text-white centerlayout">(<c:out value="${trattamento.codice}" />)</div>
    </div>
  <c:choose>
    <c:when test="${fn:endsWith(trattamento.codice, \"-T\")}">
    <div class="form-custom">
      <dl class="list-group list-group-horizontal">
        <dt class="bordo">
          <strong>Titolare del Trattamento</strong>&nbsp;
        </dt>
        <dd class="list-group-item marginLeft">
          Universit&agrave; degli Studi di Verona<br>
          Via dell’Artigliere n. 8<br>
          37129 - Verona<br>
          C.F: 93009870234<br>
          P.I. 01541040232<br>
          Tel. 0458028777<br>
          E-mail: privacy@ateneo.univr.it &nbsp; 
        </dd>
        <dt class="bordo marginLeftLarge">
          <strong>Responsabile della<br> 
          Protezione dei Dati (DPO)</strong>&nbsp;
        </dt>
        <dd class="list-group-item marginLeft">
          GL CONSULTING S.R.L.<br>
          Soggetto individuato quale referente<br> 
          per il titolare: Gianluca Lombardi<br>
          Tel. 0312242323 / 3482345012<br>
          E-mail: info@gianlucalombardi.com<br>
          PEC: gianluca.lombardi@ingpec.eu<br>
        </dd>
      </dl>
    </div>
    </c:when>
    <c:when test="${fn:endsWith(trattamento.codice, \"-R\")}">
    <div class="form-custom">
      <dl class="list-group list-group-horizontal">
        <dt class="bordo">
          <strong>Responsabile del Trattamento</strong>&nbsp;
        </dt>
        <dd class="list-group-item marginLeft">
          Universit&agrave; degli Studi di Verona<br>
          Via dell’Artigliere n. 8<br>
          37129 - Verona<br>
          C.F: 93009870234<br>
          P.I. 01541040232<br>
          Tel. 0458028777<br>
          E-mail: privacy@ateneo.univr.it       
        </dd>
        <dt class="bordo marginLeftLarge">
          <strong>Responsabile della<br> 
          Protezione dei Dati (DPO)</strong>&nbsp;
        </dt>
        <dd class="list-group-item marginLeft">
          GL CONSULTING S.R.L.<br>
          Soggetto individuato quale referente<br> 
          per il titolare: Gianluca Lombardi<br>
          Tel. 0312242323 / 3482345012<br>
          E-mail: info@gianlucalombardi.com<br>
          PEC: gianluca.lombardi@ingpec.eu<br>
        </dd>
      </dl>
    </div>
    </c:when>
    </c:choose>
    <div class="form-custom">
      <h3 class="bordo heading pHeader">Descrizione sintetica del trattamento</h3>
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
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-check2-square" viewBox="0 0 16 16">
            <path d="M3 14.5A1.5 1.5 0 0 1 1.5 13V3A1.5 1.5 0 0 1 3 1.5h8a.5.5 0 0 1 0 1H3a.5.5 0 0 0-.5.5v10a.5.5 0 0 0 .5.5h10a.5.5 0 0 0 .5-.5V8a.5.5 0 0 1 1 0v5a1.5 1.5 0 0 1-1.5 1.5H3z"/>
            <path d="m8.354 10.354 7-7a.5.5 0 0 0-.708-.708L8 9.293 5.354 6.646a.5.5 0 1 0-.708.708l3 3a.5.5 0 0 0 .708 0z"/>
          </svg>&nbsp;
          <c:out value="${attivita.nome}" escapeXml="false" />
          (<c:out value="${attivita.codice}" />)
        </li>
    </c:forEach>
      </ul>
  </c:if>
    </div>
    <hr class="separatore" />
    <div class="form-custom">
      <h3 class="bordo heading pHeader">Art. 30 1b): Descrizione delle Finalit&agrave; perseguite</h3>
      <c:out value="${trattamento.finalita}" escapeXml="false" />
      <hr class="separatore" />
      <h3 class="bordo heading pHeader bg-warning">Basi Giuridiche</h3>
      <ul>
    <c:forEach var="base" items="${trattamento.basiGiuridiche}" varStatus="status">
      <li class="list-group-item">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-book-half" viewBox="0 0 16 16">
          <path d="M8.5 2.687c.654-.689 1.782-.886 3.112-.752 1.234.124 2.503.523 3.388.893v9.923c-.918-.35-2.107-.692-3.287-.81-1.094-.111-2.278-.039-3.213.492V2.687zM8 1.783C7.015.936 5.587.81 4.287.94c-1.514.153-3.042.672-3.994 1.105A.5.5 0 0 0 0 2.5v11a.5.5 0 0 0 .707.455c.882-.4 2.303-.881 3.68-1.02 1.409-.142 2.59.087 3.223.877a.5.5 0 0 0 .78 0c.633-.79 1.814-1.019 3.222-.877 1.378.139 2.8.62 3.681 1.02A.5.5 0 0 0 16 13.5v-11a.5.5 0 0 0-.293-.455c-.952-.433-2.48-.952-3.994-1.105C10.413.809 8.985.936 8 1.783z"/>
        </svg>&nbsp;
        <c:out value="${base.nome}" escapeXml="false" />
      <c:choose>
        <c:when test="${base.codice eq 'C'}">(DATI COMUNI)</c:when>
        <c:when test="${base.codice eq 'P'}">(DATI PARTICOLARI)</c:when> 
      </c:choose>
      <br>
      <cite><c:out value="${base.descrizione}" escapeXml="false" /></cite>
      <br>
      <cite><c:out value="${base.informativa}" escapeXml="false" /></cite>
      </li>
    </c:forEach>
      </ul>
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
    <div class="form-custom">
      <h3 class="bordo heading pHeader">Art. 30 1c): Descrizione delle categorie di interessati</h3>
      <ul class="list-group">
      <c:forEach var="interessati" items="${trattamento.interessati}">
        <li class="list-group-item">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-people" viewBox="0 0 16 16">
            <path d="M15 14s1 0 1-1-1-4-5-4-5 3-5 4 1 1 1 1h8Zm-7.978-1A.261.261 0 0 1 7 12.996c.001-.264.167-1.03.76-1.72C8.312 10.629 9.282 10 11 10c1.717 0 2.687.63 3.24 1.276.593.69.758 1.457.76 1.72l-.008.002a.274.274 0 0 1-.014.002H7.022ZM11 7a2 2 0 1 0 0-4 2 2 0 0 0 0 4Zm3-2a3 3 0 1 1-6 0 3 3 0 0 1 6 0ZM6.936 9.28a5.88 5.88 0 0 0-1.23-.247A7.35 7.35 0 0 0 5 9c-4 0-5 3-5 4 0 .667.333 1 1 1h4.216A2.238 2.238 0 0 1 5 13c0-1.01.377-2.042 1.09-2.904.243-.294.526-.569.846-.816ZM4.92 10A5.493 5.493 0 0 0 4 13H1c0-.26.164-1.03.76-1.724.545-.636 1.492-1.256 3.16-1.275ZM1.5 5.5a3 3 0 1 1 6 0 3 3 0 0 1-6 0Zm3-2a2 2 0 1 0 0 4 2 2 0 0 0 0-4Z"/>
          </svg>&nbsp;
          <c:out value="${interessati.nome}" escapeXml="false" />
          (<c:out value="${interessati.informativa}" />)
        </li>
      </c:forEach>
      </ul>
    </div>
    <hr class="separatore" />
  </c:if>
    <div>
      <h3 class="bordo heading pHeader bg-info text-white">Art. 30 1c): Descrizione delle categorie di dati personali:</h3>            
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
            <td>Dati comuni</td>
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
    <div class="form-custom">
      <h3 class="bordo heading pHeader">Art. 30 1d): Categorie di destinatari a cui i dati vengono comunicati:</h3>
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
    <div class="form-custom">
      <h3 class="bordo heading pHeader">Art. 30 1f): Termini ultimi previsti per la cancellazione</h3>
      <c:out value="${trattamento.terminiUltimi}" escapeXml="false" />
    </div>
    <hr class="separatore" />
  <c:if test="${not empty trattamento.extraInfos.extraInfo1}">
    <div class="form-custom">
      <h3 class="bordo heading pHeader">Art. 30 1g) Descrizione generale delle misure di sicurezza tecniche ed organizzative</h3>
      <c:out value="${trattamento.extraInfos.extraInfo1}" escapeXml="false" />
    </div>
    <hr class="separatore" />
  </c:if>
  <c:if test="${not empty trattamento.extraInfos.extraInfo2}">
    <div class="form-custom">
      <h3 class="bordo heading pHeader">Luoghi di custodia dei supporti di memorizzazione</h3>
      <c:out value="${trattamento.extraInfos.extraInfo2}" escapeXml="false" />
    </div>
    <hr class="separatore" />
  </c:if>
  <c:if test="${not empty trattamento.bancheDati}">
    <div class="form-custom">
      <h3 class="bordo heading pHeader">Database</h3>
      <ul class="list-group">
      <c:forEach var="bancadati" items="${trattamento.bancheDati}">
        <li class="list-group-item">
        <c:choose>
          <c:when test="${bancadati.livello eq 1}">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-database" viewBox="0 0 16 16">
            <path d="M4.318 2.687C5.234 2.271 6.536 2 8 2s2.766.27 3.682.687C12.644 3.125 13 3.627 13 4c0 .374-.356.875-1.318 1.313C10.766 5.729 9.464 6 8 6s-2.766-.27-3.682-.687C3.356 4.875 3 4.373 3 4c0-.374.356-.875 1.318-1.313ZM13 5.698V7c0 .374-.356.875-1.318 1.313C10.766 8.729 9.464 9 8 9s-2.766-.27-3.682-.687C3.356 7.875 3 7.373 3 7V5.698c.271.202.58.378.904.525C4.978 6.711 6.427 7 8 7s3.022-.289 4.096-.777A4.92 4.92 0 0 0 13 5.698ZM14 4c0-1.007-.875-1.755-1.904-2.223C11.022 1.289 9.573 1 8 1s-3.022.289-4.096.777C2.875 2.245 2 2.993 2 4v9c0 1.007.875 1.755 1.904 2.223C4.978 15.71 6.427 16 8 16s3.022-.289 4.096-.777C13.125 14.755 14 14.007 14 13V4Zm-1 4.698V10c0 .374-.356.875-1.318 1.313C10.766 11.729 9.464 12 8 12s-2.766-.27-3.682-.687C3.356 10.875 3 10.373 3 10V8.698c.271.202.58.378.904.525C4.978 9.71 6.427 10 8 10s3.022-.289 4.096-.777A4.92 4.92 0 0 0 13 8.698Zm0 3V13c0 .374-.356.875-1.318 1.313C10.766 14.729 9.464 15 8 15s-2.766-.27-3.682-.687C3.356 13.875 3 13.373 3 13v-1.302c.271.202.58.378.904.525C4.978 12.71 6.427 13 8 13s3.022-.289 4.096-.777c.324-.147.633-.323.904-.525Z"/>
          </svg>&nbsp;
          </c:when>
          <c:when test="${bancadati.livello eq 2}">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-database-fill" viewBox="0 0 16 16">
            <path d="M3.904 1.777C4.978 1.289 6.427 1 8 1s3.022.289 4.096.777C13.125 2.245 14 2.993 14 4s-.875 1.755-1.904 2.223C11.022 6.711 9.573 7 8 7s-3.022-.289-4.096-.777C2.875 5.755 2 5.007 2 4s.875-1.755 1.904-2.223Z"/>
            <path d="M2 6.161V7c0 1.007.875 1.755 1.904 2.223C4.978 9.71 6.427 10 8 10s3.022-.289 4.096-.777C13.125 8.755 14 8.007 14 7v-.839c-.457.432-1.004.751-1.49.972C11.278 7.693 9.682 8 8 8s-3.278-.307-4.51-.867c-.486-.22-1.033-.54-1.49-.972Z"/>
            <path d="M2 9.161V10c0 1.007.875 1.755 1.904 2.223C4.978 12.711 6.427 13 8 13s3.022-.289 4.096-.777C13.125 11.755 14 11.007 14 10v-.839c-.457.432-1.004.751-1.49.972-1.232.56-2.828.867-4.51.867s-3.278-.307-4.51-.867c-.486-.22-1.033-.54-1.49-.972Z"/>
            <path d="M2 12.161V13c0 1.007.875 1.755 1.904 2.223C4.978 15.711 6.427 16 8 16s3.022-.289 4.096-.777C13.125 14.755 14 14.007 14 13v-.839c-.457.432-1.004.751-1.49.972-1.232.56-2.828.867-4.51.867s-3.278-.307-4.51-.867c-.486-.22-1.033-.54-1.49-.972Z"/>
          </svg>&nbsp;
          </c:when>
          <c:when test="${bancadati.livello eq 3}">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-fingerprint" viewBox="0 0 16 16">
            <path d="M8.06 6.5a.5.5 0 0 1 .5.5v.776a11.5 11.5 0 0 1-.552 3.519l-1.331 4.14a.5.5 0 0 1-.952-.305l1.33-4.141a10.5 10.5 0 0 0 .504-3.213V7a.5.5 0 0 1 .5-.5Z"/>
            <path d="M6.06 7a2 2 0 1 1 4 0 .5.5 0 1 1-1 0 1 1 0 1 0-2 0v.332c0 .409-.022.816-.066 1.221A.5.5 0 0 1 6 8.447c.04-.37.06-.742.06-1.115V7Zm3.509 1a.5.5 0 0 1 .487.513 11.5 11.5 0 0 1-.587 3.339l-1.266 3.8a.5.5 0 0 1-.949-.317l1.267-3.8a10.5 10.5 0 0 0 .535-3.048A.5.5 0 0 1 9.569 8Zm-3.356 2.115a.5.5 0 0 1 .33.626L5.24 14.939a.5.5 0 1 1-.955-.296l1.303-4.199a.5.5 0 0 1 .625-.329Z"/>
            <path d="M4.759 5.833A3.501 3.501 0 0 1 11.559 7a.5.5 0 0 1-1 0 2.5 2.5 0 0 0-4.857-.833.5.5 0 1 1-.943-.334Zm.3 1.67a.5.5 0 0 1 .449.546 10.72 10.72 0 0 1-.4 2.031l-1.222 4.072a.5.5 0 1 1-.958-.287L4.15 9.793a9.72 9.72 0 0 0 .363-1.842.5.5 0 0 1 .546-.449Zm6 .647a.5.5 0 0 1 .5.5c0 1.28-.213 2.552-.632 3.762l-1.09 3.145a.5.5 0 0 1-.944-.327l1.089-3.145c.382-1.105.578-2.266.578-3.435a.5.5 0 0 1 .5-.5Z"/>
            <path d="M3.902 4.222a4.996 4.996 0 0 1 5.202-2.113.5.5 0 0 1-.208.979 3.996 3.996 0 0 0-4.163 1.69.5.5 0 0 1-.831-.556Zm6.72-.955a.5.5 0 0 1 .705-.052A4.99 4.99 0 0 1 13.059 7v1.5a.5.5 0 1 1-1 0V7a3.99 3.99 0 0 0-1.386-3.028.5.5 0 0 1-.051-.705ZM3.68 5.842a.5.5 0 0 1 .422.568c-.029.192-.044.39-.044.59 0 .71-.1 1.417-.298 2.1l-1.14 3.923a.5.5 0 1 1-.96-.279L2.8 8.821A6.531 6.531 0 0 0 3.058 7c0-.25.019-.496.054-.736a.5.5 0 0 1 .568-.422Zm8.882 3.66a.5.5 0 0 1 .456.54c-.084 1-.298 1.986-.64 2.934l-.744 2.068a.5.5 0 0 1-.941-.338l.745-2.07a10.51 10.51 0 0 0 .584-2.678.5.5 0 0 1 .54-.456Z"/>
            <path d="M4.81 1.37A6.5 6.5 0 0 1 14.56 7a.5.5 0 1 1-1 0 5.5 5.5 0 0 0-8.25-4.765.5.5 0 0 1-.5-.865Zm-.89 1.257a.5.5 0 0 1 .04.706A5.478 5.478 0 0 0 2.56 7a.5.5 0 0 1-1 0c0-1.664.626-3.184 1.655-4.333a.5.5 0 0 1 .706-.04ZM1.915 8.02a.5.5 0 0 1 .346.616l-.779 2.767a.5.5 0 1 1-.962-.27l.778-2.767a.5.5 0 0 1 .617-.346Zm12.15.481a.5.5 0 0 1 .49.51c-.03 1.499-.161 3.025-.727 4.533l-.07.187a.5.5 0 0 1-.936-.351l.07-.187c.506-1.35.634-2.74.663-4.202a.5.5 0 0 1 .51-.49Z"/>
          </svg>&nbsp;
          </c:when>
          </c:choose>
          <c:out value="${bancadati.nome}" escapeXml="false" />
          (<c:out value="${bancadati.tipo}" />)
        </li>
      </c:forEach>
      </ul>
    </div>
    <hr class="separatore" />
  </c:if>
  <c:if test="${not empty trattamento.extraInfo}">
    <div class="form-custom">
      <h3 class="bordo heading pHeader">Ulteriori informazioni</h3>
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
