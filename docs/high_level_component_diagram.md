# DuoCode: High-Level Component Diagram

This diagram shows the high-level architecture of the DuoCode application, following a layered architecture pattern.

```mermaid
graph TD
    %% Define the Layers
    subgraph "User Interface Layer"
        ui[UI Components]
        screens[Screens]
        navigation[Navigation]
        themes[Theme]
    end

    subgraph "Domain/Business Logic Layer"
        viewmodels[ViewModels]
        utils[Utils]
    end

    subgraph "Data/Model Layer"
        models[Data Models]
    end

    subgraph "External Systems"
        firebase[Firebase]
        subgraph "Firebase Services"
            auth[Authentication]
            firestore[Firestore Database]
            storage[Cloud Storage]
        end
    end

    %% Define relationships
    ui --> screens
    screens --> viewmodels
    navigation --> screens
    themes --> ui
    
    viewmodels --> models
    viewmodels --> firebase
    
    firebase --> auth
    firebase --> firestore
    firebase --> storage
    
    models -.-> firestore

    %% Add styling
    classDef uiLayer fill:#d4f1f9,stroke:#05c,stroke-width:2px
    classDef domainLayer fill:#ffe6cc,stroke:#ff9933,stroke-width:2px
    classDef dataLayer fill:#e1d5e7,stroke:#9673a6,stroke-width:2px
    classDef externalLayer fill:#f5f5f5,stroke:#666,stroke-width:2px
    classDef firebaseServices fill:#d5e8d4,stroke:#82b366,stroke-width:2px

    class ui,screens,navigation,themes uiLayer
    class viewmodels,utils domainLayer
    class models dataLayer
    class firebase externalLayer
    class auth,firestore,storage firebaseServices
```

## Description

The DuoCode application follows a layered architecture with four main layers:

1. **User Interface Layer**: Contains all UI-related components, screens, navigation, and theming.
2. **Domain/Business Logic Layer**: Contains ViewModels that handle business logic and UI state.
3. **Data/Model Layer**: Contains data models that represent the application's entities.
4. **External Systems**: Contains Firebase services for authentication, database, and storage.

The architecture enforces separation of concerns where:
- UI components only interact with ViewModels
- ViewModels manage UI state and business logic
- Data Models define the structure of application data
- Firebase services handle data persistence and authentication 