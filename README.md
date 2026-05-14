# Aura Messenger 🔮

Aura es un proyecto Android nativo de mensajería de alta gama, diseñado con una estética **Premium Cyberpunk Glassmorphism**. El proyecto enfatiza la privacidad, el rendimiento y una experiencia de usuario (UX) inmersiva, priorizando interacciones fluidas, seguridad de base de datos y un diseño ultra moderno.

## 🚀 Tecnologías y Stack Oficial

Este proyecto está construido bajo los estándares más modernos de desarrollo en Android:

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose (Material 3 + Custom Glassmorphism UI)
- **Arquitectura:** Clean Architecture + MVVM
- **Patrones:** Repository Pattern, Use Cases
- **Inyección de Dependencias:** Dagger Hilt
- **Asincronía y Estado:** Coroutines, StateFlow
- **Navegación:** Navigation Compose
- **Backend (BaaS):** Firebase (Auth, Firestore, Storage)
- **Multimedia:** CameraX, Google ML Kit (Barcode Scanning), ZXing Core, Cloudinary

---

## 📂 Arquitectura de Módulos (Clean Architecture)

El proyecto está modularizado por capas lógicas dentro de `app/src/main/java/com/stokia/aura/`:

- **`core/`**: Clases base, utilidades comunes y extensiones.
- **`data/`**: Implementaciones de repositorios, DTOs y lógica de acceso a datos (Firestore, Cloudinary).
- **`domain/`**: Modelos de dominio (`AuraUser`, `AuraResult`), interfaces de repositorios y Use Cases puros.
- **`presentation/`**: Capa de UI (Jetpack Compose). Contiene pantallas (`screens/`), navegación (`navigation/`) y temas (`ui/theme/`).
- **`di/`**: Módulos de provisión de dependencias (Dagger Hilt).

---

## 🎯 Estado de Fases de Desarrollo

### ✅ Fase 1: Autenticación y Arquitectura Base
- [x] Configuración de Firebase y Hilt.
- [x] Arquitectura Clean + MVVM establecida.
- [x] Login y Registro fluido con validación de formularios.
- [x] Estilo visual Glassmorphism base.

### ✅ Fase 2: Perfil Premium y Seguridad Firestore
- [x] Subida de Avatares (1:1) y Portadas (16:9) usando Cloudinary.
- [x] Implementación de recortes profesionales con Android Image Cropper.
- [x] Lógica de creación de perfil (Selección de `@username` único).
- [x] **Seguridad:** Implementación de estrictas reglas (`firestore.rules`) para evitar sobreescritura de perfiles o robo de _usernames_.

### ✅ Fase 3: Ecosistema de Contactos y Códigos QR
- [x] Generación de QR dinámico (`aura://contact/{username}`) estilizado en colores Neon/Cyan usando ZXing.
- [x] Integración de escáner de alta velocidad nativo con CameraX y ML Kit Barcode Scanning.
- [x] Búsqueda manual por nombre de usuario.
- [x] Inserción automática de contactos en tiempo real estilo WhatsApp.

### ⏳ Fase 4: Mensajería y Cifrado (Próximamente)
- [ ] Listado de Chats.
- [ ] Implementación de Tink para Cifrado Extremo a Extremo (E2E).
- [ ] Base de Datos en Tiempo Real (RTDB / Firestore listener) para mensajes instantáneos.

---

## 🛠️ Configuración para Desarrolladores

1. **Clonar el Repositorio:** Asegúrate de abrir la carpeta raíz en Android Studio Ladybug (o superior).
2. **Firebase:** El proyecto requiere el archivo `google-services.json` configurado con el paquete `com.stokia.aura`.
3. **Firestore Rules:** Si creas un nuevo proyecto en Firebase, asegúrate de copiar el contenido de `firestore.rules` ubicado en la raíz del proyecto y pegarlo en tu consola de Firebase.
4. **Cloudinary:** Las variables de entorno para la carga de imágenes se deben configurar.
5. **Ejecutar:** Sincroniza Gradle (`Sync Project with Gradle Files`) y ejecuta la app (`Shift + F10`).

---
_Desarrollado con arquitectura sólida para el futuro de la comunicación móvil._
