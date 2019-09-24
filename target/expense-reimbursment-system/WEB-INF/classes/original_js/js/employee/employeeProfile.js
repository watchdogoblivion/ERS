(function () {
	getCurrentUserInfo();
	document.querySelector("#save_changes").addEventListener("click", saveChanges);

	function getCurrentUserInfo() {
		let jwt = jwt_decode(sessionStorage.ers_auth);
		let currentEmail = jwt.email;
		let params = `email=${currentEmail}`
		getUserAccounts(params, loadData);
	}

	function loadData(userAccounts) {
		let userAccount = userAccounts[0];
		document.querySelector("#firstName").value = userAccount.firstName;
		document.querySelector("#lastName").value = userAccount.lastName;
		document.querySelector("#email").value = userAccount.email;
		setProfileImage("#mini_profile_image", "#large_profile_image");
	}

	function saveChanges(e) {
		e.preventDefault();
		let url = `http://localhost:8080/ers/api/user-accounts`;
		let xhr = new XMLHttpRequest();
		xhr.open("PUT", url);
		xhr.setRequestHeader("Authorization", JSON.parse(sessionStorage.ers_auth));

		let formData = new FormData();
		formData.append('firstName', document.querySelector("#firstName").value);
		formData.append('lastName', document.querySelector("#lastName").value);
		formData.append('email', document.querySelector("#email").value);
		let password = document.querySelector("#password").value;
		if (password != undefined && password.trim() != "") {
			formData.append('password', password);
			formData.append('confirm_password', document.querySelector("#confirm_password").value);
		}

		formData.append('authority', jwt_decode(sessionStorage.ers_auth).authority);
		let profile_image = document.querySelector("#profile_image").files[0];
		if (profile_image != undefined) {
			formData.append('file', profile_image);
		}


		xhr.onload = function () {
			if (xhr.status == 200) {
				console.log("Updated")
				sessionStorage.imageUrl = xhr.response;
				console.log(xhr.response)
				goToPage("employeeHome", "employeeHeader", "applicationFooter");
			} else {
				console.log("Recieved status code: " + xhr.status + " " + xhr.statusText);
				let warnning = document.querySelector("#server_warning");
				warnning.innerHTML = "";
				switch (xhr.status) {
					case 401:
						sessionStorage.ers_auth = undefined;
						goToPage("login");
					case 460: warnning.innerHTML = "Invalid Password.";
						break;
					case 461: warnning.innerHTML =  "Passwords do not match";
						break;
					case 462: warnning.innerHTML =  "Invalid Email.";
						break;
					case 463: warnning.innerHTML =  "Email already exists";
						break;
					case 464: warnning.innerHTML =  "Invalid first name.";
						break;
					case 465: warnning.innerHTML = "Invalid last name";
						break;
				}
			}
		};

		xhr.send(formData);
	}
})();