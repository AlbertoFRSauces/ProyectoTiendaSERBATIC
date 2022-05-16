function validarPassword(password, repetirPassword){
    var expresionRegularPassword = /^[a-zA-Z0-9]+$/;
    var campoErrorNombre = document.getElementById("errorNombre");
    var passwordValidada = document.getElementById("passwordNueva");
    var textoError = document.createElement("a");
    var mensajeError = document.getElementById("mensajeError");
    
    if(mensajeError != null){
        mensajeError.parentNode.removeChild(mensajeError);
    }
    if(password === "" || repetirPassword === ""){
        textoError.innerHTML = "Hay campos vacios.";
        textoError.setAttribute("id", "mensajeError")
        campoErrorNombre.appendChild(textoError);
        return false;
    }
    
    if(!expresionRegularPassword.exec(passwordValidada.value)){
        textoError.innerHTML = "El campo contraseña solo admite numeros y letras.";
        textoError.setAttribute("id", "mensajeError");
        campoErrorNombre.appendChild(textoError);
        return false;
    }
    
    if(password !== repetirPassword){
        textoError.innerHTML = "Las contraseñas no coinciden.";
        textoError.setAttribute("id", "mensajeError")
        campoErrorNombre.appendChild(textoError);
        return false;
    }
}
function validarCambiarPassword(){
    var password = document.getElementById("passwordNueva").value;
    var repetirPassword = document.getElementById("passwordRepetida").value;
    return validarPassword(password, repetirPassword);
}
