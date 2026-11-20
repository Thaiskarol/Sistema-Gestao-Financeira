<#
Mede o tempo de resposta das consultas do EPIC 11 (RNF016, RNF017 e RNF019).
Autora: Mayane.

Antes de rodar: o app precisa estar no ar e a base carregada com 100.000 transacoes.

Uso (troque o 1 pelo id do usuario de carga):
  .\medir-consultas.ps1 -UsuarioId 1 -Rotulo antes
  .\medir-consultas.ps1 -UsuarioId 1 -Rotulo depois

Cada endpoint e chamado -Repeticoes vezes (padrao 5). Somente respostas HTTP 200
entram nas estatisticas; as demais aparecem como descartadas.

Saidas (na pasta atual): resultado-<rotulo>.md e resultado-<rotulo>.csv.
Ao rodar "depois", se existir resultado-antes.csv na mesma pasta, o script tambem
imprime a tabela final no modelo do grupo (Endpoint | Meta | Antes | Depois | Passou?).
#>
param(
    [Parameter(Mandatory = $true)][long]$UsuarioId,
    [Parameter(Mandatory = $true)][ValidateSet("antes", "depois")][string]$Rotulo,
    [string]$BaseUrl = "http://localhost:8080",
    [int]$Repeticoes = 5,
    [string]$MesA = "2026-09",
    [string]$MesB = "2026-10",
    [string]$DataInicio = "2026-01-01",
    [string]$DataFim = "2026-11-30",
    [double]$LimiteMs = 2000
)

$ErrorActionPreference = "Stop"
$pt  = [System.Globalization.CultureInfo]::GetCultureInfo("pt-BR")
$inv = [System.Globalization.CultureInfo]::InvariantCulture

function Formatar([double]$ms) {
    return ($ms.ToString("N2", $pt) + " ms")
}

function Medir([string]$url) {
    $sw = [System.Diagnostics.Stopwatch]::StartNew()
    $status = 0
    try {
        $resp = Invoke-WebRequest -Uri $url -Method Get -UseBasicParsing -TimeoutSec 60
        $status = [int]$resp.StatusCode
    }
    catch {
        if ($_.Exception.Response) { $status = [int]$_.Exception.Response.StatusCode }
        else { $status = -1 }
    }
    $sw.Stop()
    return [pscustomobject]@{ Status = $status; Ms = $sw.Elapsed.TotalMilliseconds }
}

$endpoints = @(
    @{ Nome = "GET /consulta/resumo";               Rota = "/consulta/resumo?usuarioId=$UsuarioId" },
    @{ Nome = "GET /gastos/por-categoria";          Rota = "/gastos/por-categoria?usuarioId=$UsuarioId" },
    @{ Nome = "GET /gastos/por-estabelecimento";    Rota = "/gastos/por-estabelecimento?usuarioId=$UsuarioId" },
    @{ Nome = "GET /gastos/por-periodo";            Rota = "/gastos/por-periodo?usuarioId=$UsuarioId&dataInicio=$DataInicio&dataFim=$DataFim" },
    @{ Nome = "GET /evolucao/despesas";             Rota = "/evolucao/despesas?usuarioId=$UsuarioId" },
    @{ Nome = "GET /evolucao/receitas";             Rota = "/evolucao/receitas?usuarioId=$UsuarioId" },
    @{ Nome = "GET /evolucao/comparativo";          Rota = "/evolucao/comparativo?usuarioId=$UsuarioId&mesA=$MesA&mesB=$MesB" }
)

$linhas = @()
$csv    = @()

foreach ($ep in $endpoints) {
    Write-Host ""
    Write-Host "== $($ep.Nome) ==" -ForegroundColor Cyan
    $url = "$BaseUrl$($ep.Rota)"
    $tempos = @()

    for ($i = 1; $i -le $Repeticoes; $i++) {
        $m = Medir $url
        if ($m.Status -eq 200) {
            $tempos += $m.Ms
            Write-Host ("  Medicao {0}: HTTP {1} - {2}" -f $i, $m.Status, (Formatar $m.Ms))
        }
        else {
            Write-Host ("  Medicao {0}: HTTP {1} - descartada ({2})" -f $i, $m.Status, (Formatar $m.Ms)) -ForegroundColor Yellow
        }
    }

    if ($tempos.Count -gt 0) {
        $media = ($tempos | Measure-Object -Average).Average
        $menor = ($tempos | Measure-Object -Minimum).Minimum
        $maior = ($tempos | Measure-Object -Maximum).Maximum
        $linhas += "| ``$($ep.Nome)`` | $([int]$LimiteMs) | $(Formatar $media) / $(Formatar $maior) | $($tempos.Count) de $Repeticoes |"
        $csv += [pscustomobject]@{
            Endpoint = $ep.Nome
            Validas  = $tempos.Count
            MediaMs  = $media.ToString("F2", $inv)
            MenorMs  = $menor.ToString("F2", $inv)
            MaiorMs  = $maior.ToString("F2", $inv)
        }
    }
    else {
        $linhas += "| ``$($ep.Nome)`` | $([int]$LimiteMs) | sem medicao valida | 0 de $Repeticoes |"
    }
}

$saida = @()
$saida += "Medicao '$Rotulo' (media / maior)"
$saida += ""
$saida += "| Endpoint | Meta (ms) | Media / Maior | Requisicoes validas (HTTP 200) |"
$saida += "|---|---:|---:|---:|"
$saida += $linhas

# Tabela final no modelo do grupo (somente ao rodar "depois" e se existir o CSV de "antes")
if ($Rotulo -eq "depois" -and (Test-Path "resultado-antes.csv")) {
    $antes = Import-Csv "resultado-antes.csv"
    $saida += ""
    $saida += "Tabela no modelo do grupo"
    $saida += ""
    $saida += "| Endpoint | Meta (ms) | Antes da V7 (média / maior) | Depois da V7 (média / maior) | Passou? |"
    $saida += "|---|---:|---:|---:|:---:|"
    foreach ($d in $csv) {
        $a = $antes | Where-Object { $_.Endpoint -eq $d.Endpoint } | Select-Object -First 1
        $depoisMaior = [double]::Parse($d.MaiorMs, $inv)
        $passou = if ($depoisMaior -le $LimiteMs) { "Sim" } else { "Não" }
        if ($a) {
            $colAntes = "$(Formatar ([double]::Parse($a.MediaMs, $inv))) / $(Formatar ([double]::Parse($a.MaiorMs, $inv)))"
        }
        else {
            $colAntes = "-"
        }
        $colDepois = "$(Formatar ([double]::Parse($d.MediaMs, $inv))) / $(Formatar $depoisMaior)"
        $saida += "| ``$($d.Endpoint)`` | $([int]$LimiteMs) | $colAntes | $colDepois | $passou |"
    }
}

Write-Host ""
Write-Host "===== TABELA (copie para o epic11-mayane.md) =====" -ForegroundColor Green
$saida | ForEach-Object { Write-Host $_ }

$saida | Out-File -FilePath "resultado-$Rotulo.md" -Encoding utf8
$csv   | Export-Csv -Path "resultado-$Rotulo.csv" -NoTypeInformation -Encoding UTF8

Write-Host ""
Write-Host "Arquivos gerados: resultado-$Rotulo.md e resultado-$Rotulo.csv"