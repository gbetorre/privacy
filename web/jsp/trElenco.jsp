<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="trattamenti" value="${requestScope.registro}" scope="page" />
    <h3 class="mt-1 m-0 font-weight-bold">Registro Trattamenti</h3>
<%--     <a href="${riCSV}" class="float-right" title="Scarica il database completo del registro dei rischi corruttivi"> --%>
<%--       <i class="fas fa-download"></i>Scarica tutti i dati -->
<!--     </a> --%>
    <hr class="riga"/>
    <!-- <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css /> -->
    <div>
      <div class="row">
        <div class="col-md-offset-1">
          <div class="panel">
            <div class="panel-heading">
              <div class="panel-body table-responsive">
                <table class="table table-bordered table-hover table-sm" id="listTr">
                  <thead class="thead-light">
                    <tr>
                      <th width="4%">Tipologia</th>
                      <th width="2%">#</th>
                      <th width="*">Trattamento</th>
                      <th width="4%">Codice</th>
                      <th width="4%">View</th>
                    </tr>
                  </thead>
                  <tbody>
                  <c:forEach var="trattamento" items="${trattamenti}" varStatus="status">
                    <tr class="active">
                      <td class="bgcolor1" width="4%">
                    <c:choose>
                      <c:when test="${fn:endsWith(trattamento.codice, \"-T\")}">
                        Titolare
                      </c:when>
                      <c:when test="${fn:endsWith(trattamento.codice, \"-R\")}">
                        Responsabile
                      </c:when>
                    </c:choose>
                      </td>
                      <td  width="2%">${status.count}</td>
                      <td  width="*">            
                        <a href="${initParam.appName}/?q=tr&idT=${trattamento.codice}&r=${param['r']}">
                          <c:out value="${trattamento.nome}" />
                        </a>
                      </td>
                      <td width="4%">${trattamento.codice}</td>
                      <td width="4%">
                        <a href="${initParam.appName}/?q=tr&idT=${trattamento.codice}&r=${param['r']}" class="btn btn-sm btn-success">
                          <i class="fa fa-search"></i>
                        </a>
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
          "oLanguage": {
              "sSearch": "Filtra:"
              },
          "searchPanes": {
              "viewTotal": false
              }
        });
      });
    </script>
