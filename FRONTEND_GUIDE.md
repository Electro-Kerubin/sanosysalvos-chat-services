# Guía: Cómo Integrar una Alerta de Estafa en el Frontend (React)

Aquí tienes una guía paso a paso para crear un componente de advertencia reutilizable en una aplicación de React. Este componente mostrará una alerta debajo de los mensajes de chat que han sido marcados como potenciales estafas por el backend.

### Prerrequisito

El backend ahora incluye un campo booleano `esPotencialEstafa` en el objeto de cada mensaje que se recibe del API. Un objeto de mensaje (`mensaje`) se verá así:

```json
{
  "idMensaje": 123,
  "idConversacion": 1,
  "idRemitente": "user@example.com",
  "contenido": "Depositame 100 y te lo llevo.",
  "leido": false,
  "esPropio": false,
  "esPotencialEstafa": true, // ¡Este es nuestro nuevo campo!
  "createdAt": "2023-10-27T10:00:00Z"
}
```

---

### Paso 1: Crear el Componente de Advertencia (`ScamWarning.js`)

Primero, creamos un componente pequeño y reutilizable que solo se encargará de mostrar el mensaje de advertencia.

1.  Crea un nuevo archivo en tu carpeta de componentes: `src/components/ScamWarning.js`.
2.  Añade el siguiente código:

```jsx
// src/components/ScamWarning.js
import React from 'react';

// Estilos básicos para la alerta. Puedes moverlos a un archivo CSS.
const warningStyle = {
  backgroundColor: '#fffbe6', // Un amarillo pálido
  borderColor: '#ffe58f',
  border: '1px solid',
  borderRadius: '8px',
  padding: '12px',
  marginTop: '8px',
  fontSize: '0.9em',
  color: '#8a6d3b',
};

const ScamWarning = () => {
  return (
    <div style={warningStyle}>
      <strong>¡Atención!</strong> Este mensaje podría ser una estafa. Nunca entregues dinero o datos personales (como contraseñas) antes de tener a tu mascota de vuelta y verificar todo en persona.
    </div>
  );
};

export default ScamWarning;
```

---

### Paso 2: Modificar el Componente del Mensaje (`ChatMessage.js`)

Ahora, necesitamos que el componente que renderiza cada burbuja de chat (`ChatMessage`) muestre la advertencia si es necesario.

1.  Asumamos que tienes un componente llamado `ChatMessage.js` que recibe un objeto `message` como prop.
2.  Importa el nuevo componente `ScamWarning`.
3.  Usa el campo `message.esPotencialEstafa` para renderizar condicionalmente el componente de advertencia.

Este sería un ejemplo de cómo podría verse tu componente de mensaje modificado:

```jsx
// src/components/ChatMessage.js
import React from 'react';
import ScamWarning from './ScamWarning'; // Importamos nuestro componente de advertencia

// Estilos para diferenciar el mensaje del usuario y el de otros.
const messageStyle = {
  padding: '10px 15px',
  borderRadius: '20px',
  marginBottom: '5px',
  maxWidth: '70%',
  color: 'white',
};

const ChatMessage = ({ message, esPropio }) => {
  const align = esPropio ? 'right' : 'left';
  const backgroundColor = esPropio ? '#007bff' : '#e9e9eb';
  const color = esPropio ? 'white' : 'black';

  return (
    <div style={{ textAlign: align, marginBottom: '10px' }}>
      <div style={{ ...messageStyle, backgroundColor, color }}>
        {message.contenido}
      </div>
      
      {/* --- LÓGICA DE LA ADVERTENCIA --- */}
      {/* Si el mensaje es marcado como estafa y no es un mensaje propio, muestra la alerta */}
      {message.esPotencialEstafa && !esPropio && (
        <ScamWarning />
      )}
    </div>
  );
};

export default ChatMessage;
```

### Explicación del Código

-   **`import ScamWarning from './ScamWarning';`**: Importamos el componente que acabamos de crear.
-   **`{message.esPotencialEstafa && !esPropio && <ScamWarning />}`**: Esta es la parte clave. Es una renderización condicional que dice:
    -   Si `message.esPotencialEstafa` es `true` **Y**
    -   El mensaje `!esPropio` (no es tuyo, ya que no tiene sentido advertirte de tus propios mensajes),
    -   Entonces, renderiza el componente `<ScamWarning />`.

---

### Paso 3: Integración en la Lista de Mensajes

No necesitas hacer cambios en el componente que renderiza la lista de mensajes (ej. `ChatWindow.js` o `MessageList.js`), siempre y cuando ya esté mapeando los mensajes y pasándolos al componente `ChatMessage`.

Tu código de renderizado de lista probablemente se vea así, y no necesita cambios:

```jsx
// src/components/MessageList.js (Ejemplo)

// ... (importaciones y resto del componente)

const MessageList = ({ messages, currentUserEmail }) => {
  return (
    <div className="message-list">
      {messages.map(msg => (
        <ChatMessage 
          key={msg.idMensaje} 
          message={msg} 
          esPropio={msg.idRemitente === currentUserEmail} 
        />
      ))}
    </div>
  );
};
```

¡Y eso es todo! Con estos cambios, cada vez que el API devuelva un mensaje con `esPotencialEstafa: true`, tu aplicación de React mostrará automáticamente una útil y clara advertencia al usuario, ayudando a prevenir estafas.
