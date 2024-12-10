# Leer el archivo .env y establecer las variables de entorno
$envFilePath = ".env"
if (Test-Path $envFilePath) {
    Get-Content $envFilePath | ForEach-Object {
        if ($_ -match "^\s*([^=]+?)\s*=\s*(.+?)\s*$") {
            [System.Environment]::SetEnvironmentVariable($matches[1], $matches[2])
        }
    }
} else {
    Write-Host "El archivo .env no se encontró en el directorio actual."
    exit 1
}

# Ejecutar el comando Maven
mvn clean package