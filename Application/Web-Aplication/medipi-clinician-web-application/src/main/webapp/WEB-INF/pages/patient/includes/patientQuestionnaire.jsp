<script type="text/javascript" charset="utf8" src="/js/patient/includes/patient.questionnaire.js"></script>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="accordion-section">
	<div class="accordion-head" id="accordion-head">
		<a href="#" class="on" aria-expanded="true" id="${param.canvasId}Header"><c:out value="${param.displayName}"/></a>
	</div>
	<div class="accordion-body form-horizontal" style="display: block">
		<div class="row">
			<div class="col-sm-10">
				<canvas id="${param.canvasId}" width="100%" height="25%" />
			</div>
			<div class="col-sm-2">
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	var includeObject = {patientUUID : '${param.patientUUID}', attributeId: '${param.attributeId}', attributeName : '${param.attributeName}', recordingDeviceType : '${param.recordingDeviceType}', displayName : '${param.displayName}', canvasId : '${param.canvasId}'};
	measurement.initChart(includeObject);
</script>