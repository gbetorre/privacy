<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="trattamenti" value="${requestScope.registro}" scope="page" />
    <h3 class="mt-1 m-0 font-weight-bold float-left">Registro Trattamenti</h3>
    <a href="${trPDF}" class="float-right badge badge-pill lightTable" style="top:-10px;" title="Scarica il registro completo dei trattamenti (PDF)" onclick="pleaseWait();">
      <i class="fas fa-download"></i>Scarica Registro
    </a><br/>
    <hr class="riga"/>
    <div>
      <div class="row">
        <div class="col-md-offset-1">
          <div class="panel">
            <div class="panel-heading">
              <div class="panel-body table-responsive">
                <table class="table table-bordered table-hover table-sm" id="listTr">
                  <thead class="thead-light">
                    <tr>
                      <th width="5%">Tipologia</th>
                      <th width="2%">#</th>
                      <th width="*">Trattamento</th>
                      <th width="10%">Codice</th>
                      <th width="14%">Azioni</th>
                    </tr>
                  </thead>
                  <tbody>
                  <c:forEach var="trattamento" items="${trattamenti}" varStatus="status">
                    <tr class="active">
                      <td width="5%" class="bgcolor1">
                    <c:choose>
                      <c:when test="${fn:endsWith(trattamento.codice, \"-T\")}">
                        Titolare
                      </c:when>
                      <c:when test="${fn:endsWith(trattamento.codice, \"-R\")}">
                        Responsabile
                      </c:when>
                    </c:choose>
                      </td>
                      <td width="2%">${status.count}</td>
                      <td width="*">
                        <a href="${initParam.appName}/?q=tr&idT=${trattamento.codice}&r=${param['r']}" title="Vedi dettagli Trattamento">
                          <c:out value="${trattamento.nome}" />
                        </a>
                      </td>
                      <td width="10%">${trattamento.codice}</td>
                      <td width="14%">
                        <ul class="action-list">
                          <li>
                            <a href="${initParam.appName}/?q=tr&idT=${trattamento.codice}&r=${param['r']}" class="btn btn-sm btn-success" title="Vedi dettagli Trattamento">
                              <i class="fa-regular fa-eye"></i>
                            </a>
                          </li>
                          <li>&nbsp;<a href="${initParam.appName}/data?q=tr&idT=${trattamento.codice}&r=${param['r']}&out=pdf" class="btn btn-warning" style="color:black;" title="Stampa Trattamento (PDF)"><i class="fa fa-print"></i></a></li>
                          <li>&nbsp;<a href="#" class="btn btn-primary" title="Modifica dettagli Trattamento"><i class="fa fa-pencil-alt"></i></a></li>
                        </ul>
                      </td>
                    </tr>
                  </c:forEach>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <script type="text/javascript">
      $(document).ready(function() {
        $('#listTr').DataTable({
          "ordering": true,  
          "paging": true,
          "bInfo": true,
          "searchPanes": {
              "viewTotal": false
              },
          "language": {
              "url": "//cdn.datatables.net/plug-ins/1.10.18/i18n/Italian.json"
            }
        });
      });
    </script>
    <script>
      function pleaseWait() {
        setTimeout(function() { 
          alert("Generazione del registro dei trattamenti in corso.\nSarà necessario attendere qualche minuto..."); 
        }, 900);
      }
    </script>

