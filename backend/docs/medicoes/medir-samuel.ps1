param(
    [Parameter(Mandatory)] [long] $U,
    [Parameter(Mandatory)] [ValidateSet("antes","depois")] [string] $Fase
)
$ErrorActionPreference = 'Stop'
$ProgressPreference = 'SilentlyContinue'
$base = "http://localhost:8080"
$endpoints = [ordered]@{
    "GET /patrimonio/evolucao"     = "/patrimonio/evolucao?usuarioId=$U"
    "GET /score"                   = "/score?usuarioId=$U"
    "GET /score/classificacao"     = "/score/classificacao?usuarioId=$U"
    "GET /insights/aumento-gastos" = "/insights/aumento-gastos?usuarioId=$U"
    "GET /metas"                   = "/metas?usuarioId=$U"
}
$resultado = foreach ($nome in $endpoints.Keys) {
    $url = $base + $endpoints[$nome]
    Write-Host "Medindo $nome ..."
    Invoke-WebRequest $url -UseBasicParsing | Out-Null
    $tempos = 1..5 | ForEach-Object {
        (Measure-Command { Invoke-WebRequest $url -UseBasicParsing | Out-Null }).TotalMilliseconds
    }
    $s = $tempos | Measure-Object -Average -Maximum
    [pscustomobject]@{
        Endpoint = $nome
        MediaMs  = [math]::Round($s.Average)
        MaiorMs  = [math]::Round($s.Maximum)
        Tempos   = ($tempos | ForEach-Object { [math]::Round($_) }) -join " / "
    }
}
$resultado | Format-Table -AutoSize
$resultado | Export-Csv "$PSScriptRoot\samuel-$Fase.csv" -NoTypeInformation -Encoding UTF8
Write-Host "Salvo em docs\medicoes\samuel-$Fase.csv" -ForegroundColor Green