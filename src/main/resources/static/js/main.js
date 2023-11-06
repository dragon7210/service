'use strict';

var singleUploadForm = document.querySelector('#singleUploadForm');
var singleFileUploadInput = document.querySelector('#singleFileUploadInput');
var singleFileUploadError = document.querySelector('#singleFileUploadError');
var singleFileUploadSuccess = document.querySelector('#singleFileUploadSuccess');
var showSQL = document.querySelector("#showSQL");
var sqlTable = document.querySelector("#sqlTable");

var multipleUploadForm = document.querySelector('#multipleUploadForm');
var multipleFileUploadInput = document.querySelector('#multipleFileUploadInput');
var multipleFileUploadError = document.querySelector('#multipleFileUploadError');
var multipleFileUploadSuccess = document.querySelector('#multipleFileUploadSuccess');

function uploadSingleFile(file) {
	var formData = new FormData();
	formData.append("file", file);

	var xhr = new XMLHttpRequest();
	xhr.open("POST", "/uploadFile");

	xhr.onload = function() {
		var response = JSON.parse(xhr.responseText);
		if (xhr.status == 200) {
			singleFileUploadError.style.display = "none";
			singleFileUploadSuccess.innerHTML = "<p>File Uploaded Successfully.</p><p>DownloadUrl : <a href='" + response.fileDownloadUri + "' target='_blank'>" + response.fileDownloadUri + "</a></p>";
			singleFileUploadSuccess.style.display = "block";
		} else {
			singleFileUploadSuccess.style.display = "none";
			singleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
		}
	}

	xhr.send(formData);
}

function uploadMultipleFiles(files) {
	var formData = new FormData();
	for (var index = 0; index < files.length; index++) {
		formData.append("files", files[index]);
	}

	var xhr = new XMLHttpRequest();
	xhr.open("POST", "/uploadMultipleFiles");

	xhr.onload = function() {
		var response = JSON.parse(xhr.responseText);
		if (xhr.status == 200) {
			multipleFileUploadError.style.display = "none";
			var content = "<p>All Files Uploaded Successfully</p>";
			for (var i = 0; i < response.length; i++) {
				content += "<p>DownloadUrl : <a href='" + response[i].fileDownloadUri + "' target='_blank'>" + response[i].fileDownloadUri + "</a></p>";
			}
			multipleFileUploadSuccess.innerHTML = content;
			multipleFileUploadSuccess.style.display = "block";
		} else {
			multipleFileUploadSuccess.style.display = "none";
			multipleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
		}
	}

	xhr.send(formData);
}

singleUploadForm.addEventListener('submit', function(event) {

	var files = singleFileUploadInput.files;
	if (files.length === 0) {
		singleFileUploadError.innerHTML = "Please select a file";
		singleFileUploadError.style.display = "block";
	}
	uploadSingleFile(files[0]);
	event.preventDefault();
}, true);


function sqlTableFunction() {
	var xhr = new XMLHttpRequest();
	xhr.open("GET", "/showSql");
	xhr.onload = function() {
		var response = JSON.parse(xhr.responseText);
		if (xhr.status === 200) {
			var data = "<table class=\"table\" border=\"1\">\n" +
				"\t\t\t\t<th>No</th>\n" +
				"\t\t\t\t<th>First name</th>\n" +
				"\t\t\t\t<th>Last name</th>\n" +
				"\t\t\t\t<th>Address1</th>\n" +
				"\t\t\t\t<th>City</th>\n" +
				"\t\t\t\t<th>State</th>\n" +
				"\t\t\t\t<th>Zip/Postal code</th>\n" +
				"\t\t\t\t<th>Country</th>\n";

			for (var i = 0; i < response.length; i++) {
				var k = i+1;
				data += "<tr>"+
					"<td>"+k+"</td>"+
					"<td>"+response[i].firstname+"</td>"+
					"<td>"+response[i].lastname+"</td>"+
					"<td>"+response[i].address1+"</td>"+
					"<td>"+response[i].city+"</td>"+
					"<td>"+response[i].state+"</td>"+
					"<td>"+response[i].zip_code+"</td>"+
					"<td>"+response[i].country+"</td>"+
					"</tr>"

			}
			data+="</table>";
			sqlTable.innerHTML = data;

		}
	}
	xhr.send();
}


showSQL.addEventListener('click', function(event) {
	sqlTableFunction();
	event.preventDefault();
}, true);
