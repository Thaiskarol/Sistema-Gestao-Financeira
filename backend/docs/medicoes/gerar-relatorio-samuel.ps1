$antes  = Import-Csv "$PSScriptRoot\samuel-antes.csv"
$arqDepois = "$PSScriptRoot\samuel-depois.csv"
$depois = if (Test-Path $arqDepois) { Import-Csv $arqDepois } else { @() }
$cpu = (Get-CimInstance Win32_Processor | Select-Object -First 1).Name.Trim()
$ram = [math]::Round((Get-CimInstance Win32_ComputerSystem).TotalPhysicalMemory / 1GB)
$data = Get-Date -Format "yyyy-MM-dd"

$linhas = @()
$linhas += "# Epic 11 — Medições do Samuel (RNF016, RNF017, RNF019)"
$linhas += ""
$linhas += "- Volume: 1 usuário, 100.000 transações (24 meses), 12 categorias, 3 orçamentos, 3 metas"
$linhas += "- Data: $data"
$linhas += "- Máquina: $cpu, $ram GB RAM, PostgreSQL 18.6, Java 24.0.2 (compilado em modo 21)"
$linhas += "- Método: app com show-sql=false; 1 chamada de aquecimento descartada; 5 medições por endpoint (Measure-Command no PowerShell)"
$linhas += ""
$linhas += "| Endpoint | Meta (ms) | Antes da V7 (média / maior) | Depois da V7 (média / maior) | Passou? |"
$linhas += "| --- | --- | --- | --- | --- |"
foreach ($a in $antes) {
    $d = $depois | Where-Object { $_.Endpoint -eq $a.Endpoint }
    if ($d) {
        $colDepois = "$($d.MediaMs) / $($d.MaiorMs)"
        $passou = if ([int]$d.MaiorMs -le 2000) { "Sim" } else { "Não" }
    } else { $colDepois = "pendente"; $passou = "pendente" }
    $linhas += "| ``$($a.Endpoint)`` | 2000 | $($a.MediaMs) / $($a.MaiorMs) | $colDepois | $passou |"
}
$linhas += ""
$linhas += "## Observações"
$linhas += ""
$linhas += "- Patrimônio, score e insights carregam todas as transações do usuário (findByUsuarioId) e agregam em memória; por isso o índice da V7 tem efeito limitado nesses endpoints."
$linhas += "- O score carrega as transações duas vezes (direto e via evolução patrimonial)."
$linhas += "- RNF019 (tempo real): atendido pelo desenho do sistema — tudo é calculado no momento da consulta, nada fica armazenado."
$linhas += "- Tempos individuais de cada medição: ver samuel-antes.csv e samuel-depois.csv nesta pasta."

$md = Join-Path $PSScriptRoot "epic11-samuel.md"
[IO.File]::WriteAllText($md, ($linhas -join "`n"), (New-Object System.Text.UTF8Encoding($false)))
Write-Host "Gerado: $md" -ForegroundColor Green