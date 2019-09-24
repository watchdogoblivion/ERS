(function () {

	sessionStorage.pendingPageNumber = 1;
	sessionStorage.resolvedPageNumber = 1;
	prepareTableRequest("#pending_tbody");
	prepareTableRequest("#resolved_tbody");
	document.querySelector("#pending_select").addEventListener("click", () => { sessionStorage.pendingPageNumber = 1;sessionStorage.resolvedPageNumber = 1;})
	document.querySelector("#resolved_select").addEventListener("click", () => {sessionStorage.pendingPageNumber = 1;sessionStorage.resolvedPageNumber = 1;})
	document.querySelector("#update_pending").addEventListener("click", () => { prepareTableRequest("#pending_tbody"); })
	document.querySelector("#update_resolved").addEventListener("click", () => { prepareTableRequest("#resolved_tbody"); })

	function prepareTableRequest(tableBodyselector) {
		const url = "http://localhost:8080/ers/api/reimbursements";
		let columnMap;
		let rowLimit;
		let pageNumber;
		switch (tableBodyselector) {
			case "#pending_tbody":
				columnMap = "status=PENDING&";
				rowLimit = document.querySelector("#pending_select").value;
				pageNumber = sessionStorage.pendingPageNumber;
				break;
			case "#resolved_tbody":
				columnMap = "status=RESOLVED&";
				rowLimit = document.querySelector("#resolved_select").value;
				pageNumber = sessionStorage.resolvedPageNumber;
				break;
		}
		const orderBy = "r_id ASC";
		const offset = rowLimit * (pageNumber - 1);
		populateTable(url, columnMap, orderBy, rowLimit, offset, tableBodyselector, addRowToTable, undefined, undefined, prepareTableRequest);
	}

	function addRowToTable(data, tableBodyselector) {
		let row =
			`<tr><td>$${data.amount}</td>
			<td>${data.status}</td>`;
		if (tableBodyselector == "#resolved_tbody") {
			row += `<td>${data.state}</td>`;
		}
		row += `<td>${data.dateSubmitted}</td>
			<td>${data.employeeAccount.firstName + " " + data.employeeAccount.lastName}</td>`;
		if (tableBodyselector == "#pending_tbody") {
			row += `<td><button id="approve_${data.id}" type="button" class="btn btn-primary btn-center btn-size">Approve</button></td>
					<td><button id="deny_${data.id}" type="button" class="btn btn-primary btn-center btn-size">Deny</button></td></tr>`;
			document.querySelector(tableBodyselector).insertAdjacentHTML("beforeend", row);
			let jwt = jwt_decode(sessionStorage.ers_auth);
			let currentEmail = jwt.email;
			let approvedMap = `id=${data.id}&email=${currentEmail}&state=${"APPROVED"}`;
			let deniedMap = `id=${data.id}&email=${currentEmail}&state=${"DENIED"}`;
			document.querySelector(`#approve_${data.id}`).addEventListener("click", () => { approve(approvedMap, prepareTableRequest.bind(this, tableBodyselector)); });
			document.querySelector(`#deny_${data.id}`).addEventListener("click", () => { approve(deniedMap, prepareTableRequest.bind(this, tableBodyselector)); });
		} else if (tableBodyselector == "#resolved_tbody") {
			row += `<td>${data.managerAccount.firstName + " " + data.managerAccount.lastName}</td></tr>`;
			document.querySelector(tableBodyselector).innerHTML += row;
		}
	}

})();