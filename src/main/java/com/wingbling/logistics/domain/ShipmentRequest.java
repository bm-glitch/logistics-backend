<!DOCTYPE html>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
<meta name="theme-color" content="#1577cc">
<meta name="mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="default">
<meta name="apple-mobile-web-app-title" content="WISLAND">
<link rel="manifest" href="manifest.json">
<link rel="apple-touch-icon" href="icon-180.png">
<script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.min.css" rel="stylesheet">
<title>업무 요청 대장부</title>
<style>
  :root {
    --blue-400:#1da4ff; --blue-500:#1577cc; --blue-600:#1166b0; --blue-700:#0e5390;
    --r-sm:6px; --r-md:12px; --r-lg:20px;
    --bg: #f2f4f6;
    --surface: #ffffff;
    --surface-alt: #f2f4f6;
    --border: #e3e3e8;
    --text: #2A2C2F;
    --text-muted: #6b6b71;
    --accent: #1577cc;
    --accent-soft: #ebf5fd;
    --danger: #d6173a;
    --danger-soft: #fce8ea;
    --warn: #b5730a;
    --warn-soft: #fbf0d9;
    --good: #12925a;
    --good-soft: #e6f4ec;
    --info: #0e5390;
    --info-soft: #e6eef7;
    --shadow: 0 1px 3px rgba(20,24,30,0.06), 0 4px 12px rgba(20,24,30,0.08);
    --sidebar-bg: #17171c;
    --sidebar-border: #292932;
    --sidebar-text: #a9adb8;
    --sidebar-text-active: #ffffff;
    --sidebar-active-bg: #1577cc;
  }
  @media (prefers-color-scheme: dark) {
    :root:not([data-theme="light"]) {
      --bg:#17171c; --surface:#202028; --surface-alt:#292932; --border:#313742;
      --text:#f3f4f6; --text-muted:#a9adb8;
      --accent:#1da4ff; --accent-soft:#0b4172;
      --danger:#ef6d7f; --danger-soft:#3a2422;
      --warn:#e7b75c; --warn-soft:#3a2e17;
      --good:#46c66a; --good-soft:#173327;
      --info:#5cb4ee; --info-soft:#12233a;
      --shadow:0 1px 3px rgba(0,0,0,0.4), 0 4px 12px rgba(0,0,0,0.45);
      --sidebar-bg:#101014; --sidebar-border:#26262e; --sidebar-text:#a9adb8; --sidebar-text-active:#ffffff; --sidebar-active-bg:#1577cc;
    }
  }
  :root[data-theme="dark"] {
    --bg:#17171c; --surface:#202028; --surface-alt:#292932; --border:#313742;
    --text:#f3f4f6; --text-muted:#a9adb8;
    --accent:#1da4ff; --accent-soft:#0b4172;
    --danger:#ef6d7f; --danger-soft:#3a2422;
    --warn:#e7b75c; --warn-soft:#3a2e17;
    --good:#46c66a; --good-soft:#173327;
    --info:#5cb4ee; --info-soft:#12233a;
    --shadow:0 1px 3px rgba(0,0,0,0.4), 0 4px 12px rgba(0,0,0,0.45);
    --sidebar-bg:#101014; --sidebar-border:#26262e; --sidebar-text:#a9adb8; --sidebar-text-active:#ffffff; --sidebar-active-bg:#1577cc;
  }

  * { box-sizing: border-box; }
  html, body { height: 100%; }
  body {
    margin: 0;
    background: var(--bg);
    color: var(--text);
    font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, "Apple SD Gothic Neo", "Malgun Gothic", "Segoe UI", sans-serif;
    line-height: 1.5;
  }
  .num { font-variant-numeric: tabular-nums; font-family: ui-monospace, SFMono-Regular, "Cascadia Mono", Consolas, monospace; }
  button { font-family: inherit; }
  svg.icon { width: 18px; height: 18px; stroke: currentColor; fill: none; stroke-width: 2; stroke-linecap: round; stroke-linejoin: round; flex-shrink: 0; }

  .app { display: flex; min-height: 100vh; }

  /* ---------- Sidebar ---------- */
  .sidebar {
    width: 232px;
    flex-shrink: 0;
    background: var(--sidebar-bg);
    border-right: 1px solid var(--sidebar-border);
    display: flex;
    flex-direction: column;
    padding: 18px 12px;
    position: sticky;
    top: 0;
    height: 100vh;
  }
  .brand {
    display: flex;
    align-items: center;
    gap: 9px;
    padding: 8px 10px 20px;
    color: var(--sidebar-text-active);
    cursor: pointer;
  }
  .brand .mark {
    width: 30px; height: 30px;
    border-radius: 8px;
    background: linear-gradient(135deg, var(--sidebar-active-bg), #6E9BF5);
    display: flex; align-items: center; justify-content: center;
    color: #fff;
    flex-shrink: 0;
  }
  .brand .mark svg { width: 16px; height: 16px; stroke: #fff; }
  .brand .name { font-size: 15px; font-weight: 700; letter-spacing: -0.01em; }

  nav.navlist { display: flex; flex-direction: column; gap: 2px; margin-top: 4px; }
  .navitem {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 9px 10px;
    border-radius: 8px;
    color: var(--sidebar-text);
    font-size: 13.5px;
    font-weight: 500;
    cursor: pointer;
    border: none;
    background: transparent;
    text-align: left;
    width: 100%;
  }
  .navitem svg { opacity: 0.85; }
  .navitem:hover { background: rgba(255,255,255,0.06); color: #fff; }
  .navitem.active { background: var(--sidebar-active-bg); color: #fff; }
  .navitem.active svg { opacity: 1; }
  .navitem .badge {
    margin-left: auto;
    background: var(--danger);
    color: #fff;
    font-size: 10.5px;
    font-weight: 700;
    border-radius: 999px;
    padding: 1px 6px;
  }
  .navsep { height: 1px; background: var(--sidebar-border); margin: 8px 10px; }

  .sidebar-foot {
    margin-top: auto;
    padding: 12px 10px;
    border-top: 1px solid var(--sidebar-border);
    color: var(--sidebar-text);
    font-size: 11.5px;
  }
  .sidebar-foot .num { color: var(--sidebar-text-active); }
  .sidebar-foot .databtns { display: flex; gap: 6px; margin-top: 10px; }
  .sidebar-foot .databtn {
    flex: 1; font-size: 11px; font-weight: 700; border-radius: 7px; padding: 6px 0;
    border: 1px solid var(--sidebar-border); background: rgba(255,255,255,0.04); color: var(--sidebar-text);
    cursor: pointer;
  }
  .sidebar-foot .databtn:hover { color: #fff; border-color: var(--sidebar-active-bg); }

  /* ---------- Main ---------- */
  .main { flex: 1; min-width: 0; }

  .topbar {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 14px 28px;
    border-bottom: 1px solid var(--border);
    background: var(--surface);
    position: sticky;
    top: 0;
    z-index: 5;
  }
  .searchbox {
    flex: 1;
    max-width: 360px;
    display: flex;
    align-items: center;
    gap: 8px;
    background: var(--surface-alt);
    border: 1px solid var(--border);
    border-radius: 9px;
    padding: 7px 12px;
    color: var(--text-muted);
  }
  .searchbox svg { width: 16px; height: 16px; }
  .searchbox input {
    border: none; background: transparent; outline: none;
    color: var(--text); font-size: 13.5px; width: 100%;
  }
  .searchbox input::placeholder { color: var(--text-muted); }

  .topbar-right { margin-left: auto; display: flex; align-items: center; gap: 14px; }
  .bellbtn {
    position: relative;
    border: 1px solid var(--border);
    background: var(--surface-alt);
    border-radius: 9px;
    width: 34px; height: 34px;
    display: flex; align-items: center; justify-content: center;
    color: var(--text-muted);
    cursor: pointer;
  }
  .bellbtn:hover { color: var(--text); border-color: var(--accent); }
  .bellbtn .dot {
    position: absolute; top: -4px; right: -4px;
    background: var(--danger); color: #fff;
    font-size: 10px; font-weight: 700;
    border-radius: 999px; min-width: 16px; height: 16px;
    display: flex; align-items: center; justify-content: center;
    padding: 0 3px;
  }
  .updated-line { font-size: 12px; color: var(--text-muted); text-align: right; }
  .updated-line .num { color: var(--text); font-weight: 600; }

  .content { padding: 24px 28px 48px; }

  h1.pagetitle { font-size: 21px; font-weight: 700; margin: 0 0 4px; letter-spacing: -0.01em; }
  .pagesub { color: var(--text-muted); font-size: 13.5px; margin-bottom: 22px; }

  /* ---------- Buttons ---------- */
  .btn {
    font-size: 13px; font-weight: 700; border-radius: 9px; padding: 8px 15px;
    cursor: pointer; border: 1px solid transparent; display: inline-flex;
    align-items: center; gap: 6px; white-space: nowrap;
  }
  .btn svg { width: 15px; height: 15px; stroke: currentColor; fill: none; stroke-width: 2.2; stroke-linecap: round; stroke-linejoin: round; }
  .btn.primary { background: var(--accent); color: #fff; }
  .btn.primary:hover { filter: brightness(1.07); }
  .btn.ghost { background: var(--surface); border-color: var(--border); color: var(--text); }
  .btn.ghost:hover { border-color: var(--accent); color: var(--accent); }
  .btn.subtle { background: var(--surface-alt); color: var(--text-muted); }
  .btn.subtle:hover { color: var(--text); }
  .btn.danger { background: var(--danger-soft); color: var(--danger); }
  .btn.small { padding: 5px 11px; font-size: 12px; font-weight: 600; border-radius: 7px; }
  .iconbtn {
    width: 34px; height: 34px; border-radius: 8px; border: 1px solid var(--border);
    background: var(--surface); color: var(--text-muted); cursor: pointer;
    display: flex; align-items: center; justify-content: center;
  }
  .iconbtn:hover:not(:disabled) { color: var(--danger); border-color: var(--danger); }
  .iconbtn:disabled { opacity: 0.35; cursor: not-allowed; }

  /* Stat cards */
  .stats { display: grid; grid-template-columns: repeat(5, 1fr); gap: 14px; margin-bottom: 22px; }
  @media (max-width: 1180px) { .stats { grid-template-columns: repeat(3, 1fr); } }
  @media (max-width: 700px) { .stats { grid-template-columns: repeat(2, 1fr); } }
  .stat {
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: 12px;
    padding: 16px 18px;
    box-shadow: var(--shadow);
    display: flex;
    align-items: flex-start;
    gap: 14px;
  }
  .stat .badge-icon {
    width: 42px; height: 42px;
    border-radius: 10px;
    display: flex; align-items: center; justify-content: center;
    flex-shrink: 0;
  }
  .stat .badge-icon svg { width: 20px; height: 20px; stroke: #fff; }
  .stat.total .badge-icon { background: var(--accent); }
  .stat.done .badge-icon { background: var(--good); }
  .stat.progress .badge-icon { background: var(--warn); }
  .stat.pending .badge-icon { background: var(--accent); }
  .stat.overdue .badge-icon { background: var(--danger); }
  .stat.missing .badge-icon { background: var(--info); }
  .stat .label { font-size: 12.5px; color: var(--text-muted); margin-bottom: 3px; }
  .stat .value { font-size: 24px; font-weight: 700; line-height: 1.1; }
  .stat .value .unit { font-size: 13px; font-weight: 500; color: var(--text-muted); margin-left: 2px; }
  .stat .sub { font-size: 11.5px; color: var(--text-muted); margin-top: 3px; }

  /* Two-column panels */
  /* '업무 진행률' 패널을 없애서 '오늘의 업무' 하나만 남았습니다 → 한 단으로 넓게 */
  .panels { display: grid; grid-template-columns: 1fr; gap: 16px; margin-bottom: 22px; }
  .panel {
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: 12px;
    padding: 16px 18px 8px;
    box-shadow: var(--shadow);
  }
  .panel-head {
    display: flex; align-items: center; gap: 8px;
    margin-bottom: 10px;
  }
  .panel-head .title { font-size: 14.5px; font-weight: 700; }
  .panel-head .count-pill {
    background: var(--accent-soft); color: var(--accent);
    font-size: 11.5px; font-weight: 700;
    border-radius: 999px; padding: 1px 8px;
  }
  .panel-head .link {
    margin-left: auto; font-size: 12.5px; color: var(--accent);
    background: none; border: none; cursor: pointer; font-weight: 600;
  }
  .panel-head .link:hover { text-decoration: underline; }
  .tablecard-head .link {
    font-size: 12px; font-weight: 700; color: var(--accent);
    background: var(--accent-soft); border: none; border-radius: 999px;
    padding: 4px 11px; cursor: pointer;
  }
  .tablecard-head .link:hover { filter: brightness(0.95); }

  .urgentrow {
    display: flex; align-items: center; gap: 10px;
    padding: 9px 4px;
    border-bottom: 1px solid var(--border);
    font-size: 13px;
  }
  .urgentrow:last-child { border-bottom: none; }
  .urgentrow input[type="checkbox"] { width: 15px; height: 15px; accent-color: var(--good); flex-shrink: 0; }
  .urgentrow .title { flex: 1; min-width: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
  .urgentrow .who { color: var(--text-muted); font-size: 12px; flex-shrink: 0; width: 64px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
  .panel-empty { padding: 22px 4px; text-align: center; color: var(--text-muted); font-size: 13px; }

  /* Donut */
  .donut-wrap { display: flex; align-items: center; gap: 18px; padding: 6px 2px 14px; }
  .legend { display: flex; flex-direction: column; gap: 9px; font-size: 12.5px; }
  .legend .row { display: flex; align-items: center; gap: 7px; }
  .legend .dot { width: 9px; height: 9px; border-radius: 50%; flex-shrink: 0; }
  .legend .name { color: var(--text-muted); }
  .legend .n { margin-left: auto; font-weight: 700; }

  /* Table */
  .tablecard {
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: 12px;
    box-shadow: var(--shadow);
    overflow: hidden;
  }
  .tablecard-head {
    display: flex; align-items: center; gap: 14px; flex-wrap: wrap;
    padding: 14px 18px; border-bottom: 1px solid var(--border);
  }
  .tablecard-head .title { font-size: 14.5px; font-weight: 700; margin-right: 6px; }
  .tabs { display: flex; gap: 4px; background: var(--surface-alt); border-radius: 8px; padding: 3px; }
  .tab {
    border: none; background: transparent; color: var(--text-muted);
    font-size: 12.5px; font-weight: 600; padding: 5px 12px; border-radius: 6px; cursor: pointer;
  }
  .tab.active { background: var(--surface); color: var(--accent); box-shadow: var(--shadow); }
  .chipfilters { display: flex; gap: 6px; flex-wrap: wrap; margin-left: auto; }
  .chip {
    border: 1px solid var(--border); background: var(--surface); color: var(--text-muted);
    border-radius: 999px; padding: 4px 11px; font-size: 12px; cursor: pointer;
  }
  .chip.active { background: var(--accent-soft); border-color: var(--accent); color: var(--accent); font-weight: 600; }
  .selectfilter {
    border: 1px solid var(--border); background: var(--surface); color: var(--text);
    border-radius: 8px; padding: 4px 10px; font-size: 12.5px; cursor: pointer;
  }
  .searchpanel { margin-bottom: 18px; }
  .searchpanel-body { display: flex; align-items: center; gap: 14px 18px; flex-wrap: wrap; padding-bottom: 4px; }
  .sp-field { display: flex; align-items: center; gap: 6px; }
  .sp-field label { font-size: 12px; font-weight: 600; color: var(--text-muted); white-space: nowrap; }
  .sp-tilde { color: var(--text-muted); }
  input.datefilter { cursor: text; }
  @media (max-width: 700px) { .searchpanel-body { flex-direction: column; align-items: stretch; } .sp-field { flex-wrap: wrap; } }

  .tablescroll { overflow-x: auto; }
  table.reqtable { width: 100%; border-collapse: collapse; font-size: 13px; min-width: 1520px; }
  table.reqtable th {
    text-align: left; font-size: 11.5px; text-transform: uppercase; letter-spacing: 0.03em;
    color: var(--text-muted); font-weight: 600; padding: 10px 14px; border-bottom: 1px solid var(--border);
    white-space: nowrap;
  }
  table.reqtable td {
    padding: 11px 14px; border-bottom: 1px solid var(--border); vertical-align: middle;
  }
  table.reqtable tr:last-child td { border-bottom: none; }
  table.reqtable td.msg { max-width: 240px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
  table.reqtable tr.mainrow { cursor: pointer; }
  table.reqtable tr.mainrow:hover td { background: var(--surface-alt); }
  .expandmark {
    display: inline-flex; align-items: center; justify-content: center;
    width: 16px; height: 16px; margin-right: 6px; color: var(--text-muted);
    vertical-align: -3px;
  }
  .expandmark svg { width: 13px; height: 13px; stroke: currentColor; fill: none; stroke-width: 2.4; stroke-linecap: round; stroke-linejoin: round; transition: transform 0.15s ease; }
  tr.mainrow.open .expandmark { color: var(--accent); }
  tr.mainrow.open .expandmark svg { transform: rotate(90deg); }
  .missing-tag {
    display: inline-block; margin-left: 6px; font-size: 10.5px; font-weight: 700;
    color: var(--info); background: var(--info-soft); border-radius: 5px; padding: 1px 6px; white-space: nowrap;
  }
  .local-tag {
    display: inline-block; margin-left: 6px; font-size: 10.5px; font-weight: 700;
    color: var(--text-muted); background: var(--surface-alt); border: 1px solid var(--border);
    border-radius: 5px; padding: 0 5px; white-space: nowrap;
  }
  .more-tag { color: var(--text-muted); font-size: 12px; }
  .stockpill {
    display: inline-block; margin-left: 6px; font-size: 10.5px; font-weight: 700;
    color: var(--accent); background: var(--accent-soft); border-radius: 5px; padding: 1px 6px; white-space: nowrap;
  }
  .stockpill:empty { display: none; }

  .statuspill { font-size: 11.5px; font-weight: 700; border-radius: 999px; padding: 2px 9px; white-space: nowrap; }
  .statuspill.pending { background: var(--accent-soft); color: var(--accent); }
  .statuspill.in_progress { background: var(--warn-soft); color: var(--warn); }
  .statuspill.done { background: var(--good-soft); color: var(--good); }
  .statuspill.cancelled { background: var(--surface-alt); color: var(--text-muted); text-decoration: line-through; }
  .trackbadge { font-family: 'JetBrains Mono', ui-monospace, monospace; font-size: 11px; font-weight: 600;
    color: var(--accent); background: var(--accent-soft); border-radius: 5px; padding: 2px 7px; white-space: nowrap; }
  .actioncell { white-space: nowrap; }
  .actioncell .btn.small { padding: 3px 9px; font-size: 12px; margin-right: 4px; }

  .billpill { font-size: 11.5px; font-weight: 700; border-radius: 999px; padding: 2px 9px; white-space: nowrap; }
  .billpill.paid { background: var(--info-soft); color: var(--info); }
  .billpill.free { background: var(--surface-alt); color: var(--text-muted); }

  .duecell.overdue { color: var(--danger); font-weight: 700; }
  .duecell.soon { color: var(--warn); font-weight: 700; }
  /* 안전재고 화면에서는 수령자·수령처·송장 열을 숨김 (해당 없음) */
  .reqtable.hide-supply .col-recv,
  .reqtable.hide-supply .col-to,
  .reqtable.hide-supply .col-track { display: none; }
  /* '비고(메모)' 열 — 관리 열을 대체해 모든 목록에서 표시 */
  .reqtable td.memocell { cursor: pointer; max-width: 220px; }
  .detail-actions-row { display: flex; gap: 8px; flex-wrap: wrap; align-items: center; }
  .reqtable td.memocell .memo-text { color: var(--warn); font-weight: 600; display: inline-block; max-width: 210px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; vertical-align: middle; }
  .reqtable td.memocell .memo-empty { color: var(--text-muted); font-size: 12.5px; }
  .reqtable td.memocell:hover .memo-empty { color: var(--accent); }
  /* 기본 목록 간소화 — 상세 정보(요청일자·판매처·가용재고·구분·수령자·수령처·완료일시)는
     행을 펼쳤을 때 상세보기에서 확인하고, 목록에서는 숨겨서 오른쪽 잘림을 없앱니다. */
  .reqtable .col-extra { display: none; }

  /* ===== 보기 전환 (목록 / 캘린더 / 칸반) ===== */
  .viewswitch { display:inline-flex; background:var(--surface-alt,#eef1f4); border:1px solid var(--border); border-radius:9px; padding:3px; gap:3px; }
  .viewswitch .vbtn { border:none; background:transparent; padding:6px 12px; border-radius:7px; font-size:12.5px; font-weight:700; color:var(--text-muted); cursor:pointer; white-space:nowrap; }
  .viewswitch .vbtn.on { background:var(--surface,#fff); color:var(--accent); box-shadow:0 1px 3px rgba(0,0,0,.10); }

  /* ===== 캘린더 ===== */
  .calbar { display:flex; align-items:center; gap:12px; margin:10px 4px 12px; flex-wrap:wrap; }
  .calbar .cnav { display:flex; align-items:center; gap:8px; }
  .calbar .cnav button { border:1px solid var(--border); background:var(--surface); width:32px; height:32px; border-radius:8px; cursor:pointer; color:var(--text); font-size:15px; }
  .calbar .cmon { font-size:15px; font-weight:800; min-width:110px; text-align:center; }
  .calbar .ctoday { border:1px solid var(--border); background:var(--surface); border-radius:8px; padding:6px 12px; font-size:12.5px; font-weight:700; cursor:pointer; color:var(--text); }
  .callegend { display:flex; gap:12px; font-size:12px; color:var(--text-muted); align-items:center; flex-wrap:wrap; margin-left:auto; }
  .cdot { display:inline-block; width:9px; height:9px; border-radius:3px; margin-right:4px; vertical-align:-1px; }
  .caldow { display:grid; grid-template-columns:repeat(7,1fr); border:1px solid var(--border); border-bottom:none; border-radius:10px 10px 0 0; overflow:hidden; background:var(--surface-alt,#fafbfc); }
  .caldow div { padding:8px 10px; font-size:11.5px; font-weight:700; color:var(--text-muted); }
  .caldow div:first-child { color:#d6473a; } .caldow div:last-child { color:#3f76d6; }
  .calgrid { display:grid; grid-template-columns:repeat(7,1fr); border:1px solid var(--border); border-top:none; border-radius:0 0 10px 10px; overflow:hidden; }
  .ccell { min-height:96px; border-right:1px solid var(--border); border-bottom:1px solid var(--border); padding:6px 7px; display:flex; flex-direction:column; gap:4px; background:var(--surface); }
  .ccell:nth-child(7n) { border-right:none; }
  .ccell.out { background:var(--surface-alt,#fafafb); }
  .cdaynum { font-size:12px; font-weight:700; color:var(--text-muted); }
  .ccell.ctodaycell .cdaynum { background:var(--accent); color:#fff; border-radius:50%; width:21px; height:21px; display:flex; align-items:center; justify-content:center; }
  .cchip { font-size:11px; font-weight:700; padding:2px 7px; border-radius:6px; cursor:pointer; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
  .cchip.pending { background:var(--accent-soft); color:var(--accent); }
  .cchip.in_progress { background:var(--warn-soft); color:var(--warn); }
  .cchip.done { background:var(--good-soft,#e4f5ec); color:var(--good,#12925a); }
  .cmore { font-size:10.5px; color:var(--accent); cursor:pointer; font-weight:700; text-decoration:underline; }
  .cmore:hover { color:var(--accent); opacity:.8; }
  .daylist { display:flex; flex-direction:column; gap:14px; margin-top:8px; max-height:60vh; overflow-y:auto; }
  .dayteam { display:flex; flex-direction:column; gap:6px; }
  .dayteam-head { font-size:13px; font-weight:700; color:var(--text); padding:2px 2px 4px; border-bottom:1px solid var(--border); }
  .dayteam-head .dayteam-cnt { color:var(--accent); margin-left:4px; }
  .dayrow { display:flex; align-items:center; gap:10px; padding:9px 11px; border:1px solid var(--border); border-radius:9px; cursor:pointer; background:var(--surface); }
  .dayrow:hover { border-color:var(--accent); background:var(--accent-soft); }
  .dayrow .dayrow-id { font-weight:700; font-variant-numeric:tabular-nums; white-space:nowrap; }
  .dayrow .dayrow-sum { flex:1; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
  .dayrow .dayrow-qty { white-space:nowrap; font-weight:600; }
  @media (max-width:640px){ .ccell{ min-height:72px; } .cchip{ font-size:10px; padding:2px 5px; } .callegend{ margin-left:0; } }

  /* ===== 칸반 ===== */
  .kanban { display:grid; grid-template-columns:repeat(3,1fr); gap:12px; padding:10px 2px 14px; }
  .kcol { background:var(--surface-alt,#f6f7f9); border:1px solid var(--border); border-radius:12px; padding:10px; min-height:120px; }
  .kcol h4 { margin:0 0 10px; font-size:13px; display:flex; align-items:center; gap:7px; }
  .kcount { background:var(--surface); border:1px solid var(--border); border-radius:999px; padding:0 8px; font-size:11.5px; color:var(--text-muted); font-weight:700; }
  .kcard { background:var(--surface); border:1px solid var(--border); border-left:3px solid var(--accent); border-radius:9px; padding:9px 11px; margin-bottom:8px; cursor:pointer; transition:transform .05s; }
  .kcard:hover { transform:translateY(-1px); box-shadow:0 3px 10px rgba(0,0,0,.07); }
  .kcard.in_progress { border-left-color:var(--warn); } .kcard.done { border-left-color:var(--good,#12925a); }
  .kcard .ksr { font-size:12px; font-weight:800; color:var(--accent); }
  .kcard .kitem { font-size:12.5px; margin:3px 0; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
  .kcard .kmeta { font-size:11.5px; color:var(--text-muted); display:flex; gap:8px; flex-wrap:wrap; }
  .kempty { font-size:12px; color:var(--text-muted); text-align:center; padding:16px 0; }
  @media (max-width:760px){ .kanban{ grid-template-columns:1fr; } }

  /* ===== 요청 상세 팝업 (캘린더/칸반에서 한 건만 열기) ===== */
  .reqmodal-overlay { position:fixed; inset:0; background:rgba(0,0,0,.42); display:none; align-items:flex-start; justify-content:center; z-index:60; padding:24px 14px; overflow:auto; }
  .reqmodal-panel { background:var(--surface); border:1px solid var(--border); border-radius:14px; max-width:580px; width:100%; padding:16px 18px 20px; box-shadow:0 16px 50px rgba(0,0,0,.28); margin:auto; }
  .reqmodal-head { display:flex; justify-content:flex-end; gap:6px; margin-bottom:4px; }
  /* .detailgrid의 2단 레이아웃은 화면(뷰포트) 너비 기준 미디어쿼리라, 이 팝업(최대 580px)처럼
     창은 넓어도 팝업 자체가 좁을 땐 오른쪽 칸(최소 460px)에 밀려 왼쪽 칸이 짜부라집니다.
     이 팝업 안에서는 폭이 항상 좁으니 처음부터 1단으로 고정합니다. */
  .reqmodal-panel .detailgrid { grid-template-columns: 1fr; }

  table.reqtable td.num { text-align: right; }
  table.reqtable th.num { text-align: right; }
  /* 요청번호·송장은 왼쪽 정렬(제목과 같은 방향)로 맞춰 "밀려 보이는" 현상 방지 */
  table.reqtable td.reqidcell, table.reqtable td.col-track { text-align: left; }
  .table-empty { padding: 40px 20px; text-align: center; color: var(--text-muted); font-size: 13.5px; }

  /* Row detail */
  tr.detailrow > td { background: var(--surface-alt); padding: 0; border-bottom: 1px solid var(--border); }
  .detailbox { padding: 18px; display: grid; gap: 16px; max-width: 1000px; }
  .detailbox .dhead { display: flex; align-items: center; gap: 10px; }
  .detailbox .dhead .dtitle { font-size: 12.5px; font-weight: 700; color: var(--text-muted); letter-spacing: 0.04em; text-transform: uppercase; }
  .detailbox .dhead .spacer { margin-left: auto; display: flex; gap: 8px; }
  .dgrid { display: grid; grid-template-columns: repeat(auto-fit, minmax(170px, 1fr)); gap: 14px 22px; }
  .dfield .k { font-size: 10.5px; font-weight: 700; color: var(--text-muted); letter-spacing: 0.05em; text-transform: uppercase; margin-bottom: 2px; }
  .dfield .v { font-size: 13px; white-space: pre-wrap; word-break: break-word; }
  .dfield .v.empty { color: var(--text-muted); }
  table.ptable {
    width: 100%; border-collapse: collapse; font-size: 12.5px;
    background: var(--surface); border: 1px solid var(--border); border-radius: 9px; overflow: hidden;
  }
  table.ptable th {
    text-align: left; font-size: 10.5px; font-weight: 700; color: var(--text-muted);
    letter-spacing: 0.04em; text-transform: uppercase;
    padding: 8px 12px; background: var(--surface-alt); border-bottom: 1px solid var(--border); white-space: nowrap;
  }
  table.ptable td { padding: 8px 12px; border-bottom: 1px solid var(--border); }
  table.ptable tr:last-child td { border-bottom: none; }
  table.ptable td.num, table.ptable th.num { text-align: right; font-variant-numeric: tabular-nums; }
  table.ptable td.stockline { white-space: nowrap; }
  table.ptable td.stockline.stock-ok { color: var(--accent); }
  table.ptable td.stockline.stock-over { color: var(--danger); font-weight: 700; }
  table.ptable td.stockline .stock-warn { font-size: 11.5px; font-weight: 700; }
  table.ptable tfoot td { background: var(--surface-alt); font-weight: 700; }
  .lspill { font-size: 11px; font-weight: 700; border-radius: 5px; padding: 2px 8px; color: #fff; white-space: nowrap; }
  .lspill.ls-hold { background: var(--warn); }
  .lspill.ls-reject { background: var(--danger); }
  .lspill.ls-exch { background: var(--accent); }
  .lsreason { font-size: 12px; color: var(--text-muted); margin-left: 4px; }

  /* 상세보기 — 섹션별 카드 틀 + 라벨/값 표 */
  .dcard { background: var(--surface); border: 1px solid var(--border); border-radius: 12px; box-shadow: var(--shadow); overflow: hidden; }
  .dcard.pad { padding: 4px 16px 6px; }
  .dcard-title { display: flex; align-items: center; gap: 7px; font-size: 12px; font-weight: 700;
    color: var(--text-muted); letter-spacing: 0.04em; text-transform: uppercase;
    padding: 12px 0 10px; border-bottom: 1px solid var(--border); margin: 0 0 2px; }
  .dcard.pad .ptable { border: none; border-radius: 0; margin: 6px 0 8px; }
  .drow { display: grid; grid-template-columns: 132px 1fr; gap: 6px 16px; padding: 11px 0;
    border-bottom: 1px solid var(--border); align-items: start; }
  .drow:last-child { border-bottom: none; }
  .drow .k { font-size: 12px; font-weight: 600; color: var(--text-muted); letter-spacing: 0.02em; padding-top: 1px; }
  .drow .v { font-size: 13.5px; color: var(--text); white-space: pre-wrap; word-break: break-word; line-height: 1.5; }
  .drow .v.empty { color: var(--text-muted); }
  .drow input[type="text"] { font-family: inherit; font-size: 13px; padding: 7px 10px;
    border: 1px solid var(--border); border-radius: 8px; background: var(--surface); color: var(--text); max-width: 220px; }
  .drow input[type="text"]:focus { border-color: var(--accent); outline: none; box-shadow: 0 0 0 3px var(--accent-soft); }
  @media (max-width: 640px) { .drow { grid-template-columns: 1fr; gap: 2px; padding: 9px 0; } }

  /* ---------- Request form ---------- */
  .formwrap { display: grid; gap: 16px; max-width: 940px; }
  .formpage { display: grid; grid-template-columns: minmax(0, 940px) 320px; gap: 20px; align-items: start; }
  .formpage .formwrap { max-width: none; }
  .stockpanel {
    position: sticky; top: 20px;
    background: var(--surface); border: 1px solid var(--border); border-radius: 12px;
    overflow: hidden; display: flex; flex-direction: column; max-height: calc(100vh - 40px);
  }
  @media (max-width: 1280px) {
    .formpage { grid-template-columns: 1fr; }
    .stockpanel { position: static; max-height: none; }
  }
  .logisticswrap { display: block; }

  /* ===== 엑셀 작업실 ===== */
  .exceltoolwrap { display: flex; flex-direction: column; gap: 10px; height: calc(100vh - 140px); }
  .xt-toolbar {
    display: flex; align-items: center; flex-wrap: wrap; gap: 6px; padding: 10px;
    background: var(--surface); border: 1px solid var(--border); border-radius: 12px; box-shadow: var(--shadow);
  }
  .xt-btn {
    appearance: none; cursor: pointer; font-family: inherit; font-size: 12.5px; font-weight: 600;
    padding: 7px 12px; border-radius: 8px; border: 1px solid var(--border); background: var(--surface); color: var(--text);
    white-space: nowrap; transition: .12s;
  }
  .xt-btn:hover:not(:disabled) { border-color: var(--accent); color: var(--accent); }
  .xt-btn:disabled { opacity: .4; cursor: not-allowed; }
  .xt-btn.primary { background: var(--accent); color: #fff; border-color: var(--accent); }
  .xt-btn.primary:hover:not(:disabled) { filter: brightness(1.07); color: #fff; }
  .xt-sep { width: 1px; align-self: stretch; background: var(--border); margin: 0 2px; }
  .xt-spacer { flex: 1; }
  .xt-headerlabel { display: flex; align-items: center; gap: 6px; font-size: 12px; color: var(--text-muted); white-space: nowrap; }
  .xt-headerlabel input { width: 52px; padding: 6px 8px; border: 1px solid var(--border); border-radius: 7px; background: var(--surface); color: var(--text); font-size: 12.5px; }
  .xt-filetabs { display: flex; gap: 6px; flex-wrap: wrap; }
  .xt-filetab {
    display: flex; align-items: center; gap: 8px; padding: 6px 8px 6px 12px; border-radius: 999px;
    border: 1px solid var(--border); background: var(--surface); font-size: 12.5px; color: var(--text-muted); cursor: pointer;
  }
  .xt-filetab.active { border-color: var(--accent); color: var(--accent); background: var(--accent-soft); font-weight: 700; }
  .xt-filetab .x { border: none; background: none; cursor: pointer; color: inherit; opacity: .6; font-size: 13px; line-height: 1; padding: 2px; }
  .xt-filetab .x:hover { opacity: 1; }
  .xt-status { font-size: 12.5px; color: var(--text-muted); min-height: 18px; padding: 0 2px; }
  .xt-status.ok { color: var(--success, #1a8754); font-weight: 600; }
  .xt-status.warn { color: var(--danger); font-weight: 600; }
  .xt-gridwrap {
    flex: 1; min-height: 0; background: var(--surface); border: 1px solid var(--border); border-radius: 12px;
    box-shadow: var(--shadow); overflow: hidden; display: flex; flex-direction: column;
  }
  .xt-empty { margin: auto; color: var(--text-muted); font-size: 13.5px; text-align: center; max-width: 420px; padding: 20px; }
  .xt-tablescroll { flex: 1; overflow: auto; }
  table.xt-table { border-collapse: collapse; font-size: 12.5px; }
  table.xt-table th, table.xt-table td {
    border: 1px solid var(--border); padding: 0; white-space: nowrap; min-width: 90px;
  }
  table.xt-table th {
    position: sticky; top: 0; background: var(--surface-alt, #f3f4f6); color: var(--text-muted);
    font-weight: 700; padding: 6px 8px; cursor: pointer; z-index: 2; user-select: none;
  }
  table.xt-table th.xt-colsel { color: var(--accent); background: var(--accent-soft); }
  table.xt-table th.xt-corner { position: sticky; left: 0; top: 0; z-index: 3; cursor: default; background: var(--surface-alt, #f3f4f6); }
  table.xt-table td.xt-rownum {
    position: sticky; left: 0; background: var(--surface-alt, #f3f4f6); color: var(--text-muted);
    font-weight: 600; text-align: center; padding: 6px 8px; z-index: 1; min-width: 46px;
  }
  table.xt-table td.xt-rownum.xt-rowsel { color: var(--accent); }
  table.xt-table td.xt-rownum input { vertical-align: middle; margin-right: 4px; }
  table.xt-table td.xt-cell { padding: 6px 8px; color: var(--text); outline: none; max-width: 320px; overflow: hidden; text-overflow: ellipsis; }
  table.xt-table td.xt-cell:focus { background: var(--accent-soft); overflow: visible; }
  table.xt-table tr.xt-headerrow td.xt-cell { background: var(--surface-alt, #f9fafb); font-weight: 700; }
  table.xt-table tr.xt-hidden { display: none; }
  table.xt-table td.xt-cell.xt-nomatch { background: #fdecea; }
  .fcard {
    background: var(--surface); border: 1px solid var(--border);
    border-radius: 12px; box-shadow: var(--shadow); overflow: hidden;
  }
  .fsec-head { display: flex; align-items: center; gap: 9px; padding: 13px 18px; border-bottom: 1px solid var(--border); }
  .fsec-head .t { font-size: 14px; font-weight: 700; }
  .fsec-head .hint { font-size: 12px; color: var(--text-muted); margin-left: auto; }
  .fsec-head .step {
    width: 20px; height: 20px; border-radius: 6px; background: var(--accent-soft); color: var(--accent);
    font-size: 11px; font-weight: 700; display: flex; align-items: center; justify-content: center; flex-shrink: 0;
  }
  .fsec-body { padding: 16px 18px; display: grid; gap: 15px; }
  .frow { display: grid; gap: 15px; grid-template-columns: repeat(2, 1fr); }
  .frow.three { grid-template-columns: repeat(3, 1fr); }
  @media (max-width: 780px) { .frow, .frow.three { grid-template-columns: 1fr; } }
  .field { display: flex; flex-direction: column; gap: 5px; min-width: 0; }
  .field > label { font-size: 12px; font-weight: 600; color: var(--text-muted); }
  .field .req { color: var(--danger); margin-left: 3px; }
  .field input, .field textarea {
    font-family: inherit; font-size: 13.5px; color: var(--text);
    background: var(--surface); border: 1px solid var(--border); border-radius: 8px;
    padding: 8px 10px; outline: none; width: 100%;
  }
  .field input::placeholder, .field textarea::placeholder { color: var(--text-muted); opacity: 0.75; }
  .field textarea { resize: vertical; min-height: 72px; line-height: 1.55; }
  .field input:focus, .field textarea:focus { border-color: var(--accent); box-shadow: 0 0 0 3px var(--accent-soft); }
  .field input.invalid, .field textarea.invalid { border-color: var(--danger); }
  .field .ferr { font-size: 11.5px; color: var(--danger); }
  .field .fnote { font-size: 11.5px; color: var(--text-muted); }

  .checks { display: flex; flex-wrap: wrap; gap: 8px; }
  .checkopt {
    display: inline-flex; align-items: center; gap: 7px;
    border: 1px solid var(--border); background: var(--surface);
    border-radius: 9px; padding: 7px 14px; font-size: 13px; cursor: pointer;
    user-select: none; color: var(--text);
  }
  .checkopt input { width: 15px; height: 15px; accent-color: var(--accent); margin: 0; cursor: pointer; flex-shrink: 0; }
  .checkopt:hover { border-color: var(--accent); }
  .checkopt.on { background: var(--accent-soft); border-color: var(--accent); color: var(--accent); font-weight: 600; }
  .checks.invalid .checkopt { border-color: var(--danger); }
  .otherwrap { margin-top: 9px; max-width: 320px; }

  .seg { display: inline-flex; background: var(--surface-alt); border: 1px solid var(--border); border-radius: 9px; padding: 3px; gap: 3px; width: fit-content; }
  .seg.invalid { border-color: var(--danger); }
  .segbtn {
    border: none; background: transparent; color: var(--text-muted);
    font-size: 12.5px; font-weight: 700; padding: 6px 18px; border-radius: 6px; cursor: pointer;
  }
  .segbtn:hover { color: var(--text); }
  .segbtn.sel { background: var(--surface); color: var(--accent); box-shadow: var(--shadow); }

  .qtybulk {
    display: flex; align-items: center; gap: 10px; flex-wrap: wrap;
    padding: 10px 12px; margin-bottom: 12px;
    background: var(--accent-soft); border: 1px solid var(--border); border-radius: 10px;
  }
  .qtybulk .qb-label { font-size: 12px; font-weight: 600; color: var(--text-muted); }
  .qtybulk #pBulkQty {
    width: 80px; box-sizing: border-box; padding: 7px 9px;
    border: 1px solid var(--border); border-radius: 8px; font-size: 14px;
    background: var(--surface-alt,#fff); color: var(--text);
  }
  .qtybulk .qb-hint { font-size: 11.5px; color: var(--text-muted); }
  .prows { display: grid; gap: 12px; }
  .prow {
    display: grid; gap: 10px; align-items: end;
    grid-template-columns: 26px 1fr 1.5fr 1fr 92px 34px;
    padding-bottom: 12px; border-bottom: 1px dashed var(--border);
  }
  .prow:last-child { border-bottom: none; padding-bottom: 0; }
  .prow .pidx { font-size: 12px; font-weight: 700; color: var(--text-muted); padding-bottom: 9px; }
  @media (max-width: 900px) {
    .prow { grid-template-columns: 1fr 1fr; }
    .prow .pidx { grid-column: 1 / -1; padding-bottom: 0; }
    .prow .del { grid-column: 2; justify-self: end; }
  }

  .formactions { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
  .formactions .spacer { margin-left: auto; display: flex; gap: 10px; }
  .banner {
    display: flex; align-items: center; gap: 12px; flex-wrap: wrap;
    padding: 12px 16px; border-radius: 10px; font-size: 13px; font-weight: 600;
    background: var(--good-soft); color: var(--good);
  }
  .banner.warn { background: var(--warn-soft); color: var(--warn); }
  .banner.error { background: var(--danger-soft); color: var(--danger); }
  .banner.ok { background: var(--accent-soft); color: var(--accent); }
  .btn.rowMemoBtn { border:1px solid var(--border); }
  .btn.rowMemoBtn.has-memo { background: var(--warn-soft); color: var(--warn); border-color: var(--warn); font-weight: 700; }
  .banner .bspacer { margin-left: auto; display: flex; gap: 8px; }

  .howto {
    margin-top: 20px;
    border: 1px dashed var(--border);
    border-radius: 12px;
    padding: 16px 18px;
    background: var(--surface);
    color: var(--text-muted);
    font-size: 13px;
  }
  .howto strong { color: var(--text); }

  :focus-visible { outline: 2px solid var(--accent); outline-offset: 2px; }
  @media (prefers-reduced-motion: reduce) { * { transition: none !important; } }

  /* ===== 모바일(폰) 최적화 — 사이드바를 상단 가로 메뉴로, 입력창 확대 방지 ===== */
  @media (max-width: 820px){
    .app{ flex-direction: column; }
    .sidebar{
      width:100%; height:auto; position:sticky; top:0; z-index:30;
      flex-direction:row; align-items:center; gap:8px;
      padding:8px 10px; overflow-x:auto;
    }
    .brand{ padding:4px 6px; flex-shrink:0; }
    .brand .name{ font-size:14px; white-space:nowrap; }
    nav.navlist{ flex-direction:row; gap:6px; margin-top:0; flex:1; overflow-x:auto; }
    .navitem{ width:auto; white-space:nowrap; padding:8px 12px; }
    .navitem .badge{ margin-left:6px; }
    .navsep{ display:none; }
    .sidebar-foot{ display:none; }
    .topbar{ padding:12px 14px; gap:10px; }
    .searchbox{ max-width:none; }
    .formpage{ gap:14px; }
    /* iOS: 입력창 폰트 16px 미만이면 포커스 시 화면이 확대돼요 → 16px로 고정 */
    input[type="text"], input[type="number"], input[type="date"], input[type="tel"], textarea, select{ font-size:16px; }
    /* 모바일: 재고검색을 상품정보 위로 올렸을 때 결과 목록이 화면을 다 먹지 않게 */
    #stockSearchResults{ max-height:46vh; }
    /* 상단바: 줄바꿈 허용 + 엑셀·업데이트 숨김 + '요청서 작성'을 크고 앞쪽(왼쪽 위)으로 */
    .topbar{ flex-wrap:wrap; padding:10px 14px; }
    .topbar .searchbox{ order:2; flex-basis:100%; max-width:none; }
    .topbar-right{ order:1; width:100%; flex-wrap:wrap; gap:8px; justify-content:flex-start; }
    #exportExcelBtn, #exportSelectedBtn, .updated-line{ display:none; }
    #newRequestBtn{ order:-1; flex:1 1 auto; justify-content:center; font-size:15px; font-weight:700; padding:12px 14px; }
    #themeToggle{ order:1; }
    #bellBtn{ order:2; }
    /* 대시보드 가로 짤림 방지: 페이지가 화면보다 넓어지지 않게 */
    html, body{ overflow-x:hidden; max-width:100%; }
    .app{ max-width:100%; overflow-x:hidden; }
    .content{ padding:16px 14px 40px; min-width:0; max-width:100%; overflow-x:hidden; }
    .panels{ min-width:0; max-width:100%; }
    .panel{ min-width:0; max-width:100%; overflow:hidden; }
    /* '오늘의 업무' 항목: 한 줄에 다 보이게(제목은 …로 줄임, 담당자·상태는 항상 표시) */
    .urgentrow{ flex-wrap:nowrap; gap:8px; padding:9px 2px; }
    .urgentrow .title{ flex:1 1 auto; min-width:0; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
    .urgentrow .who{ flex-shrink:0; width:auto; max-width:70px; }
    .urgentrow .statuspill{ flex-shrink:0; }
    .panel-head{ flex-wrap:wrap; }
    .panel-head .title{ min-width:0; }
    /* '업무 진행률' 도넛+범례가 좁은 화면에서 줄바꿈 되게 */
    .donut-wrap{ flex-wrap:wrap; gap:12px; }
    /* 표는 카드 안에서만 좌우 스크롤(페이지 전체가 넓어지지 않게) */
    .tablecard{ min-width:0; max-width:100%; }
    .tablescroll{ min-width:0; max-width:100%; -webkit-overflow-scrolling:touch; }
  }

  /* =======================================================================
     v11 추가 스타일 — ① 배너 정리 ② 상태 탭 배지 ③ 페이지 이동 ④ 원본파일 미리보기
     ======================================================================= */

  /* ① 목록 중심 화면(전체 요청·완료·취소·안전재고)에서는 "확인이 필요 없는" 지표를 감춥니다.
        - 업무 진행률(도넛)·오늘의 업무 패널
        - 전체 요청·완료 카드 (시간이 지나면 계속 쌓이기만 하는 숫자)
        대시보드 화면에서는 지금처럼 전부 보입니다. */
  body.lean-banner .panels{ display:none !important; }
  body.lean-banner .howto{ display:none !important; }
  body.hide-cumulative .stat.total,
  body.hide-cumulative .stat.done{ display:none !important; }

  /* ② 상태 탭 안의 건수 배지 */
  .tab .tbadge{
    display:inline-block; min-width:17px; margin-left:5px; padding:1px 5px;
    border-radius:9px; font-size:11px; font-weight:800; line-height:1.45;
    font-variant-numeric:tabular-nums; text-align:center;
    background:var(--surface-alt,#eef1f6); color:var(--text-muted);
  }
  .tab.active .tbadge{ background:var(--accent); color:#fff; }
  .tab .tbadge.warn{ background:#FDECEA; color:#C0392B; }
  .tab.active .tbadge.warn{ background:#C0392B; color:#fff; }

  /* 상태 칸을 눌러서 바꾸는 버튼(안전재고 전용) — 알약 모양 그대로 유지 */
  button.statuspill{
    appearance:none; -webkit-appearance:none; font-family:inherit; font-size:inherit;
    cursor:pointer; border:1px dashed transparent;
  }
  button.statuspill:hover{ filter:brightness(.94); border-color:currentColor; }
  button.statuspill .arrow{ opacity:.55; margin-left:3px; font-size:11px; }

  /* ③ 페이지 이동(페이지네이션) */
  .pager{
    display:flex; align-items:center; justify-content:center; gap:6px;
    flex-wrap:wrap; padding:14px 12px; border-top:1px solid var(--border);
  }
  .pager .pinfo{ margin-right:auto; font-size:12.5px; color:var(--text-muted); font-variant-numeric:tabular-nums; }
  .pager button{
    appearance:none; cursor:pointer; font-family:inherit; font-size:13px; font-weight:700;
    min-width:34px; height:32px; padding:0 9px; border-radius:8px;
    border:1px solid var(--border); background:var(--surface); color:var(--text);
    font-variant-numeric:tabular-nums;
  }
  .pager button:hover:not(:disabled){ border-color:var(--accent); color:var(--accent); }
  .pager button.on{ background:var(--accent); border-color:var(--accent); color:#fff; }
  .pager button:disabled{ opacity:.4; cursor:not-allowed; }
  .pager .gap{ color:var(--text-muted); padding:0 2px; }
  .pager select{
    font-family:inherit; font-size:12.5px; padding:5px 8px; border-radius:8px;
    border:1px solid var(--border); background:var(--surface); color:var(--text); cursor:pointer;
  }

  /* 드래그앤드롭 업로드 배너 — 클릭도 되고, 파일을 끌어다 놓아도 됩니다 */
  .dropzone{
    display:flex; align-items:center; justify-content:center; gap:10px;
    border:1.5px dashed var(--border); border-radius:10px; padding:16px 14px;
    cursor:pointer; background:var(--surface-alt); text-align:center;
    transition:border-color .15s ease, background-color .15s ease;
  }
  .dropzone:hover{ border-color:var(--accent); }
  .dropzone.dragover{ border-color:var(--accent); background:var(--accent-soft); }
  .dropzone .dz-icon{ font-size:22px; line-height:1; flex:none; }
  .dropzone .dz-text{ font-size:13px; color:var(--text); line-height:1.5; }
  .dropzone .dz-text b{ color:var(--accent); }
  .dropzone .dz-sub{ display:block; font-size:11.5px; color:var(--text-muted); margin-top:2px; }
  .dz-input{
    position:absolute !important; width:1px; height:1px; padding:0; margin:-1px;
    overflow:hidden; clip:rect(0,0,0,0); white-space:nowrap; border:0;
  }
  .dz-filelist{ margin-top:8px; font-size:12.5px; color:var(--text-muted); display:flex; flex-wrap:wrap; align-items:center; gap:6px; }
  .dz-filelist b{ color:var(--text); }
  .filechip{
    display:inline-flex; align-items:center; gap:6px; max-width:100%;
    background:var(--surface); border:1px solid var(--border); border-radius:99px;
    padding:3px 6px 3px 10px; font-size:12px; color:var(--text);
  }
  .filechip .fc-name{ overflow:hidden; text-overflow:ellipsis; white-space:nowrap; max-width:220px; }
  .filechip .fc-x{
    flex:none; width:18px; height:18px; line-height:16px; text-align:center; padding:0;
    border:none; border-radius:50%; background:var(--surface-alt); color:var(--text-muted);
    font-size:13px; cursor:pointer;
  }
  .filechip .fc-x:hover{ background:var(--danger-soft); color:var(--danger); }
  .dz-heading{ display:flex; align-items:flex-start; gap:8px; margin:4px 0 6px; }
  .dz-heading.mt{ margin-top:16px; }
  .dz-badge{ flex:none; width:20px; height:20px; border-radius:50%; display:flex; align-items:center; justify-content:center; font-size:11px; font-weight:700; margin-top:1px; }
  .dz-heading .dz-title{ font-size:13.5px; font-weight:700; }
  .dz-heading .dz-desc{ display:block; font-size:11.5px; font-weight:400; color:var(--text-muted); margin-top:1px; }
  .dz-heading.main .dz-badge{ background:var(--accent-soft); color:var(--accent); }
  .dz-heading.main .dz-title{ color:var(--accent); }
  .dz-heading.ref .dz-badge{ background:var(--warn-soft); color:var(--warn); }
  .dz-heading.ref .dz-title{ color:var(--warn); }

  /* ④ 상세보기 2단 구성 — 왼쪽 요청 정보 / 오른쪽 요청자 원본 파일 */
  .detailgrid{ display:grid; grid-template-columns:minmax(0,1fr) minmax(460px,48%); gap:16px; align-items:start; }
  .detailgrid > div{ min-width:0; }
  @media (max-width:1400px){ .detailgrid{ grid-template-columns:minmax(0,1fr) minmax(420px,44%); } }
  @media (max-width:1000px){ .detailgrid{ grid-template-columns:1fr; } }
  .srcprev{ position:sticky; top:8px; }
  .srcprev .srcfile{ margin-bottom:12px; }
  .srcprev .srcfile:last-child{ margin-bottom:0; }
  .srcprev .srcname{
    display:flex; align-items:center; gap:6px; font-size:12.5px; font-weight:700;
    color:var(--text); margin-bottom:6px; word-break:break-all;
  }
  .srcprev .srcname a{ color:var(--accent); text-decoration:underline; font-weight:600; font-size:11.5px; flex:none; }
  .srcprev .srcwrap{ max-height:min(68vh,760px); overflow:auto; border:1px solid var(--border); border-radius:8px; background:var(--surface-alt,#fafbfc); }
  .srcprev table{ border-collapse:collapse; width:max-content; min-width:100%; font-size:11.5px; }
  .srcprev th, .srcprev td{
    border-bottom:1px solid var(--border); border-right:1px solid var(--border);
    padding:5px 8px; text-align:left; white-space:nowrap; max-width:340px;
    overflow:hidden; text-overflow:ellipsis; color:var(--text);
  }
  .srcprev thead th{
    position:sticky; top:0; z-index:1; background:var(--surface); font-weight:800;
    color:var(--text); box-shadow:inset 0 -1px 0 var(--border);
  }
  .srcprev tbody tr:nth-child(even){ background:rgba(127,127,127,.045); }
  .srcprev .rowno{ color:var(--text-muted); font-variant-numeric:tabular-nums; text-align:right; }
  .srcprev .srcnote{ font-size:11.5px; color:var(--text-muted); line-height:1.5; margin-top:7px; }
  .srcprev .srcmore{ font-size:11.5px; color:var(--text-muted); padding:6px 8px; }
  /* 원본에서 이 요청과 관계없는 줄(다중 업로드로 만들어진 다른 요청건)은 흐리게 */
  .srcprev tbody tr.other td{ opacity:.38; }

  /* 원본 ↔ 요청서 자동 대조 카드 */
  .cmp{ border:1px solid var(--border); border-radius:10px; padding:10px 11px; margin-bottom:12px; background:var(--surface); }
  .cmp-head{ display:flex; align-items:center; gap:7px; flex-wrap:wrap; margin-bottom:8px; }
  .cmp-head .t{ font-size:12.5px; font-weight:800; color:var(--text); }
  .cmp-badge{ font-size:11px; font-weight:800; padding:2px 8px; border-radius:9px; }
  .cmp-badge.ok{ background:#E7F6EC; color:#1B7F45; }
  .cmp-badge.warn{ background:#FDECEA; color:#C0392B; }
  .cmp-row{ display:grid; grid-template-columns:64px minmax(0,1fr) 16px; gap:6px; align-items:start;
            font-size:11.5px; padding:4px 0; border-top:1px dashed var(--border); }
  .cmp-row:first-of-type{ border-top:0; }
  .cmp-row .k{ color:var(--text-muted); font-weight:700; white-space:nowrap; font-size:11px; letter-spacing:-.2px; }
  .cmp-row .v{ min-width:0; color:var(--text); line-height:1.5; word-break:break-all; }
  .cmp-row .v .was{ display:block; color:#C0392B; }
  .cmp-row .v .now{ display:block; color:var(--text-muted); }
  .cmp-row .s{ text-align:right; font-size:11px; }
  .cmp-sub{ font-size:11px; color:var(--text-muted); margin:8px 0 4px; font-weight:700; }
  .cmp-item{ border-top:1px dashed var(--border); padding:5px 0; font-size:11.5px; line-height:1.5; }
  .cmp-item .lbl{ display:inline-block; min-width:38px; color:var(--text-muted); font-weight:700; }
  .cmp-item.bad{ background:#FDECEA33; }
  .cmp-item .qtybad{ color:#C0392B; font-weight:800; }
  .cmp-code{ font-variant-numeric:tabular-nums; color:var(--accent); }
</style>

<div class="app">
  <aside class="sidebar">
    <div class="brand" id="brandHome" title="대시보드로 이동">
      <div class="mark"><svg viewBox="0 0 24 24" fill="none" stroke-width="2"><rect x="4" y="4" width="16" height="16" rx="3"/><path d="M8 9h8M8 13h5"/></svg></div>
      <div class="name">업무 요청 대장부</div>
    </div>
    <nav class="navlist">
      <button class="navitem active" data-view="dashboard">
        <svg class="icon" viewBox="0 0 24 24"><path d="M4 11.5 12 5l8 6.5V19a1 1 0 0 1-1 1h-4v-6h-6v6H5a1 1 0 0 1-1-1Z"/></svg>
        대시보드
      </button>
      <button class="navitem" data-view="all">
        <svg class="icon" viewBox="0 0 24 24"><path d="M8 6h12M8 12h12M8 18h12"/><circle cx="4" cy="6" r="1.2" fill="currentColor" stroke="none"/><circle cx="4" cy="12" r="1.2" fill="currentColor" stroke="none"/><circle cx="4" cy="18" r="1.2" fill="currentColor" stroke="none"/></svg>
        전체 요청
        <span class="badge" id="navAllBadge"></span>
      </button>
      <button class="navitem" data-view="done">
        <svg class="icon" viewBox="0 0 24 24"><path d="M20 7 10 17l-5-5"/></svg>
        완료 내역
      </button>
      <button class="navitem" data-view="cancelled">
        <svg class="icon" viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="m15 9-6 6M9 9l6 6"/></svg>
        취소 내역
      </button>
      <button class="navitem" data-view="safety">
        <svg class="icon" viewBox="0 0 24 24"><path d="M12 3 4 6v6c0 4 3.5 7 8 9 4.5-2 8-5 8-9V6l-8-3Z"/></svg>
        안전재고
        <span class="badge" id="navSafetyBadge" style="background:var(--info);"></span>
      </button>
      <div class="navsep"></div>
      <button class="navitem" data-view="form">
        <svg class="icon" viewBox="0 0 24 24"><path d="M14 3H7a1 1 0 0 0-1 1v16a1 1 0 0 0 1 1h10a1 1 0 0 0 1-1V7Z"/><path d="M14 3v4h4"/><path d="M12 11v6M9 14h6"/></svg>
        요청서 작성
      </button>
      <div class="navsep"></div>
      <button class="navitem" data-view="logistics">
        <svg class="icon" viewBox="0 0 24 24"><path d="M3 7l2-3h14l2 3"/><path d="M3 7h18v12a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V7Z"/><path d="M8 11h8"/></svg>
        물류 대장
      </button>
      <div class="navsep"></div>
      <button class="navitem" data-view="exceltool">
        <svg class="icon" viewBox="0 0 24 24"><rect x="4" y="4" width="16" height="16" rx="2"/><path d="M4 10h16M10 4v16"/></svg>
        엑셀 작업실
      </button>
    </nav>
    <div class="sidebar-foot">
      마지막 업데이트<br><span class="num" id="sidebarUpdated"></span>
      <div class="databtns">
        <button type="button" class="databtn" id="exportLocalBtn" title="이 브라우저에 저장된 요청서를 파일로 내보내기" style="display:none;">내보내기</button>
        <button type="button" class="databtn" id="importLocalBtn" title="다른 컴퓨터에서 내보낸 파일을 불러오기" style="display:none;">가져오기</button>
      </div>
      <input type="file" id="importFileInput" accept="application/json" style="display:none;">
    </div>
  </aside>

  <div class="main">
    <div class="topbar">
      <div class="searchbox">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="7"/><path d="m21 21-3.5-3.5"/></svg>
        <input id="searchInput" type="text" placeholder="품목, 요청자, 요청팀, 수령자 검색...">
      </div>
      <div class="topbar-right">
        <button class="bellbtn" id="themeToggle" title="라이트/다크 전환" aria-label="테마 전환">
          <svg class="icon" viewBox="0 0 24 24"><path d="M21 12.8A9 9 0 1 1 11.2 3a7 7 0 0 0 9.8 9.8Z"/></svg>
        </button>
        <button class="btn ghost" id="exportExcelBtn" title="지금 화면에 보이는 목록 전체를 엑셀로 내려받아요">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 3v12m0 0-4-4m4 4 4-4"/><path d="M4 17v3a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1v-3"/></svg>
          전체 엑셀
        </button>
        <button class="btn ghost" id="exportSelectedBtn" title="왼쪽에서 체크한 주문건만 엑셀로 내려받아요" disabled style="opacity:.5;cursor:not-allowed;">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 11l3 3 8-8"/><path d="M20 12v7a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1h9"/></svg>
          선택 엑셀 <span id="selCount" style="font-variant-numeric:tabular-nums;">0</span>
        </button>
        <button class="btn primary" id="newRequestBtn">
          <svg viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"/></svg>
          요청서 작성
        </button>
        <button class="bellbtn" id="bellBtn" title="기한 초과 항목 보기">
          <svg class="icon" viewBox="0 0 24 24"><path d="M18 8a6 6 0 0 0-12 0c0 5-2 6-2 6h16s-2-1-2-6"/><path d="M10 20a2 2 0 0 0 4 0"/></svg>
          <span class="dot" id="bellCount">0</span>
        </button>
        <div class="updated-line">업데이트<br><span class="num" id="topUpdated"></span></div>
      </div>
    </div>

    <div class="content">
      <h1 class="pagetitle" id="pageTitle">대시보드</h1>
      <div class="pagesub" id="pageSub">슬랙으로 들어온 업무 요청을 놓치지 않고 한눈에 확인해요</div>

      <div class="stats">
        <div class="stat total">
          <div class="badge-icon"><svg class="icon" viewBox="0 0 24 24"><rect x="4" y="4" width="16" height="16" rx="3"/><path d="M8 9h8M8 13h5"/></svg></div>
          <div><div class="label">전체 요청</div><div class="value num" id="statTotal">0<span class="unit">건</span></div></div>
        </div>
        <div class="stat done">
          <div class="badge-icon"><svg class="icon" viewBox="0 0 24 24"><path d="M20 7 10 17l-5-5"/></svg></div>
          <div><div class="label">완료</div><div class="value num" id="statDone">0<span class="unit">건</span></div><div class="sub num" id="statDonePct">0%</div></div>
        </div>
        <div class="stat progress">
          <div class="badge-icon"><svg class="icon" viewBox="0 0 24 24"><circle cx="12" cy="12" r="8"/><path d="M12 8v4l3 2"/></svg></div>
          <div><div class="label">진행중</div><div class="value num" id="statProgress">0<span class="unit">건</span></div><div class="sub num" id="statProgressPct">0%</div></div>
        </div>
        <div class="stat pending">
          <div class="badge-icon"><svg class="icon" viewBox="0 0 24 24"><circle cx="12" cy="12" r="8"/><path d="M12 8v4"/><path d="M12 12h3"/></svg></div>
          <div><div class="label">대기</div><div class="value num" id="statPending">0<span class="unit">건</span></div><div class="sub num" id="statPendingPct">0%</div></div>
        </div>
        <div class="stat overdue">
          <div class="badge-icon"><svg class="icon" viewBox="0 0 24 24"><path d="M12 9v4"/><path d="M10.3 3.9 2.7 17a1.5 1.5 0 0 0 1.3 2.2h16a1.5 1.5 0 0 0 1.3-2.2L13.7 3.9a1.5 1.5 0 0 0-2.6 0Z"/><circle cx="12" cy="16.3" r="0.6" fill="currentColor" stroke="none"/></svg></div>
          <div><div class="label">기한 초과</div><div class="value num" id="statOverdue">0<span class="unit">건</span></div><div class="sub">즉시 확인 필요</div></div>
        </div>
        <div class="stat missing">
          <div class="badge-icon"><svg class="icon" viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="M9.5 9a2.5 2.5 0 0 1 5 0c0 2-2.5 2-2.5 4"/><circle cx="12" cy="17" r="0.6" fill="currentColor" stroke="none"/></svg></div>
          <div><div class="label">정보 누락</div><div class="value num" id="statMissing">0<span class="unit">건</span></div><div class="sub">품목·수량·희망일 확인</div></div>
        </div>
      </div>

      <div class="panels">
        <div class="panel">
          <div class="panel-head">
            <span class="title">오늘의 업무</span>
            <span class="count-pill num" id="todayCount">0</span>
            <button class="link" id="todaySeeAll">전체 보기 &rsaquo;</button>
          </div>
          <div id="todayList"></div>
        </div>
      </div>
      <!-- '업무 진행률'(도넛) 패널은 없앴습니다 — 완료가 계속 쌓이기만 하는 지표라
           대시보드에서도 확인할 일이 없다는 피드백 반영. (2026-08-27) -->

      <!-- ============ 요청서 작성 ============ -->
      <div class="formpage" id="formSection" style="display:none;">
       <div class="formwrap">
        <div id="formBanner"></div>

        <div class="fcard">
          <div class="fsec-head">
            <span class="step num">1</span>
            <span class="t">요청 정보</span>
            <span class="hint">요청번호는 등록할 때 자동으로 부여돼요</span>
          </div>
          <div class="fsec-body">
            <div class="field">
              <label id="reqTypeLabel">요청 종류<span class="req">*</span></label>
              <div class="seg" id="reqTypeSeg" role="group" aria-labelledby="reqTypeLabel">
                <button type="button" class="segbtn sel" data-reqtype="출고요청" aria-pressed="true">출고요청</button>
                <button type="button" class="segbtn" data-reqtype="안전재고" aria-pressed="false">안전재고(재고확보)</button>
              </div>
              <div class="fnote" id="reqTypeNote">출고요청은 실제 배송까지 진행돼요. 안전재고는 재고 확보만 요청하는 거라 수령정보·판매처가 필요 없어요.</div>
            </div>
            <div class="frow three">
              <div class="field">
                <label for="fTeam">요청팀<span class="req">*</span></label>
                <input id="fTeam" type="text" list="teamOptions" placeholder="예: 영업1팀" autocomplete="off">
                <div class="ferr" data-err="fTeam"></div>
              </div>
              <div class="field">
                <label for="fRequester">요청자<span class="req">*</span></label>
                <input id="fRequester" type="text" list="requesterOptions" placeholder="예: 이수진" autocomplete="off">
                <div class="ferr" data-err="fRequester"></div>
              </div>
              <div class="field">
                <label for="fAssignee">담당자</label>
                <input id="fAssignee" type="text" list="assigneeOptions" placeholder="미정이면 비워 두세요" autocomplete="off">
              </div>
            </div>
            <div class="frow three">
              <div class="field">
                <label for="fRequestDate">요청일자<span class="req">*</span></label>
                <input id="fRequestDate" type="date">
                <div class="ferr" data-err="fRequestDate"></div>
              </div>
              <div class="field">
                <label for="fDesiredDate">출고 희망일<span class="req">*</span></label>
                <input id="fDesiredDate" type="date">
                <div class="ferr" data-err="fDesiredDate"></div>
              </div>
              <div class="field">
                <label id="billingLabel">유상 / 무상<span class="req">*</span></label>
                <div class="seg" id="billingSeg" role="group" aria-labelledby="billingLabel">
                  <button type="button" class="segbtn" data-val="free" aria-pressed="false">무상</button>
                  <button type="button" class="segbtn" data-val="paid" aria-pressed="false">유상</button>
                </div>
                <div class="ferr" data-err="billing"></div>
              </div>
            </div>
            <div class="field">
              <label id="channelLabel">판매처<span class="req">*</span></label>
              <div class="checks" id="channelChecks" role="group" aria-labelledby="channelLabel"></div>
              <div class="otherwrap" id="channelOtherWrap" style="display:none;">
                <input id="fChannelOther" type="text" placeholder="기타 판매처를 입력해 주세요" autocomplete="off">
              </div>
              <div class="ferr" data-err="channels"></div>
            </div>
            <div class="field">
              <label for="fContent">요청내용</label>
              <textarea id="fContent" placeholder="요청 배경이나 처리 시 참고할 내용을 적어 주세요. 예: 8/2 대전 지역 행사에 사용할 판촉물이에요."></textarea>
            </div>
          </div>
        </div>

        <div class="fcard">
          <div class="fsec-head">
            <span class="t">📥 여러 배송지 한 번에 등록 (다중)</span>
            <span class="hint">배송지가 여러 곳일 때</span>
          </div>
          <div class="fsec-body">
            <div class="hint" style="margin-bottom:10px;line-height:1.6;">
              배송지가 여러 곳이면 하나씩 등록하지 말고, 아래 <b>양식</b>을 받아 채워서 올리세요.
              한 줄이 한 주문건이 되고, <b>주문건마다 각각 별도 요청서</b>로 한 번에 등록돼요.
              위 <b>요청팀·요청자·출고 희망일·유상/무상·판매처</b>는 공통으로 적용되고, <b>주소·송장은 주문건마다 따로</b> 관리돼요.
              타부서 발주서 여러 장을 한 번에 올려도 합쳐서 처리돼요.
            </div>
            <div style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:12px;">
              <button type="button" class="btn ghost small" id="dlFreeTemplate">📄 무상 양식 받기</button>
              <button type="button" class="btn ghost small" id="dlPaidTemplate">📄 유상 양식 받기</button>
            </div>

            <div class="dz-heading main"><span class="dz-badge">①</span><div><span class="dz-title">요청서 파일 업로드</span><span class="dz-desc">채운 양식을 올리면 이 파일들로 요청서를 만들어요</span></div></div>
            <label for="fBulkFile" class="dropzone" id="fBulkDrop" style="margin-top:4px;">
              <span class="dz-icon">📥</span>
              <span class="dz-text"><b>클릭</b>하거나 파일을 여기로 끌어다 놓으세요 (여러 개 가능)</span>
            </label>
            <input id="fBulkFile" type="file" multiple class="dz-input">
            <div id="fBulkFileName" class="dz-filelist"></div>
            <div id="fBulkMsg" style="margin-top:8px;font-size:13px;"></div>

            <div class="dz-heading ref mt"><span class="dz-badge">②</span><div><span class="dz-title">참고용·기타 서류 업로드</span><span class="dz-desc">주소록·안내문처럼 함께 필요한 서류 — 요청서를 만드는 데는 안 쓰고, 만들어지는 요청서 전부에 그대로 첨부돼요</span></div></div>
            <label for="fBulkRefFiles" class="dropzone" id="fBulkRefDrop">
              <span class="dz-icon">🗂</span>
              <span class="dz-text"><b>클릭</b>하거나 파일을 여기로 끌어다 놓으세요 (여러 개 가능)</span>
            </label>
            <input id="fBulkRefFiles" type="file" multiple class="dz-input">
            <div id="fBulkRefFilesName" class="dz-filelist"></div>
          </div>
        </div>

        <div class="fcard">
          <div class="fsec-head">
            <span class="t">📎 발주서로 자동 채우기 (선택)</span>
            <span class="hint">한 곳으로 여러 상품(합포장)</span>
          </div>
          <div class="fsec-body">
            <div class="hint" style="margin-bottom:8px;">
              타부서에서 받은 발주서(.xls/.xlsx)를 올리면 상품·수령정보가 자동으로 채워져요.
              <b>수령지가 같은 발주서 여러 장</b>을 동시에 올리면 상품을 합쳐 한 요청(합포장)으로 등록돼요.
              수령지가 다른 발주서를 함께 올리면 등록이 막혀요 — 그럴 땐 위쪽 <b>📥 여러 배송지 한 번에 등록 (다중)</b> 을 쓰거나 파일을 하나씩 따로 올려 주세요.
            </div>
            <div class="dz-heading main"><span class="dz-badge">①</span><div><span class="dz-title">요청서(발주서) 파일 업로드</span><span class="dz-desc">이 파일로 상품·수령정보를 채워요</span></div></div>
            <label for="fOrderFile" class="dropzone" id="fOrderDrop" style="margin-top:4px;">
              <span class="dz-icon">📎</span>
              <span class="dz-text"><b>클릭</b>하거나 파일을 여기로 끌어다 놓으세요 (여러 개 가능)<span class="dz-sub">.xls, .xlsx</span></span>
            </label>
            <input id="fOrderFile" type="file" accept=".xls,.xlsx" multiple class="dz-input">
            <div id="fOrderFileName" class="dz-filelist"></div>
            <div class="ferr" id="fOrderFileMsg" style="margin-top:6px;font-size:13px;"></div>

            <div class="dz-heading ref mt"><span class="dz-badge">②</span><div><span class="dz-title">참고용·기타 서류 업로드</span><span class="dz-desc">이 요청서에 그대로 첨부만 돼요 — 상품·수령정보를 채우는 데는 안 써요</span></div></div>
            <label for="fOrderRefFiles" class="dropzone" id="fOrderRefDrop">
              <span class="dz-icon">🗂</span>
              <span class="dz-text"><b>클릭</b>하거나 파일을 여기로 끌어다 놓으세요 (여러 개 가능)</span>
            </label>
            <input id="fOrderRefFiles" type="file" multiple class="dz-input">
            <div id="fOrderRefFilesName" class="dz-filelist"></div>
          </div>
        </div>

        <div class="fcard">
          <div class="fsec-head">
            <span class="step num">2</span>
            <span class="t">상품 정보</span>
            <span class="hint">상품은 1개 이상 등록해야 해요</span>
          </div>
          <div class="fsec-body">
            <div class="qtybulk" id="qtyBulkBar">
              <label class="checkopt" for="pPickAll" title="아래 모든 상품을 한 번에 선택/해제">
                <input type="checkbox" id="pPickAll"><span>전체 선택</span>
              </label>
              <span class="qb-label">수량</span>
              <input id="pBulkQty" type="number" min="1" step="1" value="1" inputmode="numeric" aria-label="일괄 등록할 수량">
              <button type="button" class="btn ghost small" id="pBulkApplyBtn">일괄 등록</button>
              <span class="qb-hint">체크한 상품에 같은 수량을 한 번에 채워요 (선택 안 하면 전체 적용)</span>
            </div>
            <div class="prows" id="productRows"></div>
            <div>
              <button type="button" class="btn ghost small" id="addProductBtn">
                <svg viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"/></svg>
                상품 추가
              </button>
            </div>
            <div class="ferr" data-err="products"></div>
          </div>
        </div>

        <div class="fcard">
          <div class="fsec-head">
            <span class="step num">3</span>
            <span class="t">수령 정보</span>
          </div>
          <div class="fsec-body">
            <div class="frow">
              <div class="field">
                <label for="fRcvName">수령자 이름<span class="req">*</span></label>
                <input id="fRcvName" type="text" list="receiverOptions" placeholder="예: 김수령" autocomplete="off">
                <div class="ferr" data-err="fRcvName"></div>
              </div>
              <div class="field">
                <label for="fRcvPhone">수령자 연락처<span class="req">*</span></label>
                <input id="fRcvPhone" type="tel" placeholder="예: 010-1234-5678" autocomplete="off">
                <div class="ferr" data-err="fRcvPhone"></div>
              </div>
            </div>
            <div class="field">
              <label for="fRcvAddr">수령자 주소<span class="req">*</span></label>
              <input id="fRcvAddr" type="text" placeholder="예: 대전광역시 유성구 대학로 99, 3층 물류팀" autocomplete="off">
              <div class="ferr" data-err="fRcvAddr"></div>
            </div>
            <div class="field">
              <label for="fRcvMsg">배송 메세지</label>
              <input id="fRcvMsg" type="text" placeholder="예: 부재 시 경비실에 맡겨 주세요" autocomplete="off">
            </div>
          </div>
        </div>

        <div class="fcard">
          <div class="fsec-head">
            <span class="t">📎 첨부파일 (선택)</span>
            <span class="hint">특이사항·참고자료·원본 파일 등, 물류팀이 확인할 파일을 올려요</span>
          </div>
          <div class="fsec-body">
            <label for="fAttachFiles" class="dropzone" id="fAttachDrop">
              <span class="dz-icon">🗂</span>
              <span class="dz-text"><b>클릭</b>하거나 파일을 여기로 끌어다 놓으세요 (여러 개 가능)</span>
            </label>
            <input id="fAttachFiles" type="file" multiple class="dz-input">
            <div class="hint" style="margin-top:8px;">
              여러 개 올릴 수 있어요. 등록하면 물류팀이 대장부 상세보기와 물류대장에서 파일을 열어볼 수 있어요.
              위에서 발주서를 올려 자동채우기를 했다면, 그 발주서 원본도 함께 첨부돼요.
            </div>
            <div id="fAttachList" style="margin-top:8px;font-size:13px;color:var(--text-muted);"></div>
          </div>
        </div>

        <div class="formactions">
          <div class="spacer">
            <button type="button" class="btn ghost" id="resetFormBtn">초기화</button>
            <button type="button" class="btn primary" id="submitFormBtn">대장부에 등록</button>
          </div>
        </div>
       </div>

       <!-- 이지어드민 실시간 재고 검색 — 항상 떠 있는 패널 -->
       <div class="stockpanel">
          <div style="padding:14px 16px;border-bottom:1px solid var(--border);background:var(--accent-soft);">
            <strong style="font-size:14px;color:var(--accent);">📦 이지어드민 실시간 재고 검색</strong>
          </div>
          <div style="padding:12px 16px;border-bottom:1px solid var(--border);">
            <div style="display:flex;gap:6px;">
              <input id="stockSearchInput" type="text" placeholder="상품코드 · 상품명 · 키워드"
                     style="flex:1;box-sizing:border-box;padding:9px 11px;border:1px solid var(--border);
                     border-radius:8px;font-size:13px;">
              <button type="button" id="stockSearchRefreshBtn" title="방금 등록한 상품이 안 보이면 눌러 주세요"
                      style="border:1px solid var(--border);border-radius:8px;background:var(--surface-alt);
                      cursor:pointer;font-size:15px;padding:0 10px;">🔄</button>
            </div>
            <div style="font-size:11.5px;color:var(--text-muted);margin-top:6px;">
              1차 재고 확인 → 2차 상품명 확인 → 3차 상품코드 확인, 여기서 한 번에! ·
              방금 등록한 상품은 🔄로 새로고침
            </div>
          </div>
          <div id="stockSearchResults" style="overflow-y:auto;flex:1;padding:6px 0;font-size:13px;">
            <div style="padding:20px 16px;color:var(--text-muted);text-align:center;">
              상품코드나 이름을 입력하면<br>여기에 재고가 바로 보여요.
            </div>
          </div>
       </div>
      </div>

      <datalist id="teamOptions"></datalist>
      <datalist id="requesterOptions"></datalist>
      <datalist id="assigneeOptions"></datalist>
      <datalist id="receiverOptions"></datalist>

      <!-- 상세 검색 — 기간·요청자로 좁혀서 찾아요. 전체 요청·안전재고 화면에 공통으로 적용돼요. -->
      <div class="panel searchpanel" id="searchPanel">
        <div class="panel-head">
          <span class="title">🔎 상세 검색</span>
        </div>
        <div class="searchpanel-body">
          <div class="sp-field">
            <label>기간</label>
            <select class="selectfilter" id="spDateField">
              <option value="desiredDate">출고 희망일</option>
              <option value="requestDate">요청일</option>
              <option value="completedAt">완료일</option>
            </select>
            <input type="date" class="selectfilter datefilter" id="spDateFrom">
            <span class="sp-tilde">~</span>
            <input type="date" class="selectfilter datefilter" id="spDateTo">
          </div>
          <div class="sp-field">
            <label>요청자</label>
            <select class="selectfilter" id="spRequester"><option value="">전체</option></select>
          </div>
          <div class="sp-field">
            <label>검색어</label>
            <input type="text" class="selectfilter" id="spKeyword" placeholder="요청번호·수령자·상품명 등 직접 입력" style="width:220px;">
          </div>
          <button type="button" class="btn primary small" id="spApply">검색</button>
          <button type="button" class="btn ghost small" id="spClear">초기화</button>
        </div>
      </div>

      <div class="tablecard">
        <div class="tablecard-head">
          <span class="title" id="tableTitle">최근 요청</span>
          <button class="link" id="clearTodayFilter" style="display:none;">&larr; 대시보드로 돌아가기</button>
          <div class="tabs" id="statusTabs">
            <button class="tab active" data-status="open" title="아직 처리가 끝나지 않은 건만 모아 봐요 (대기 + 진행중)">진행 필요<span class="tbadge warn" id="tabOpenCount">0</span></button>
            <button class="tab" data-status="pending">대기<span class="tbadge" id="tabPendingCount">0</span></button>
            <button class="tab" data-status="in_progress">진행중<span class="tbadge" id="tabProgressCount">0</span></button>
            <button class="tab" data-status="done">완료</button>
            <button class="tab" data-status="">전체</button>
          </div>
          <div class="viewswitch" id="viewSwitch" title="보기 방식">
            <button type="button" class="vbtn on" data-vm="calendar">📅 캘린더</button>
            <button type="button" class="vbtn" data-vm="list">☰ 목록</button>
            <button type="button" class="vbtn" data-vm="kanban">🗂 칸반</button>
          </div>
          <select class="selectfilter" id="teamFilter" title="요청팀으로 골라 봐요"><option value="">전체 팀</option></select>
          <select class="selectfilter" id="assigneeFilter"><option value="">담당자 전체</option></select>
          <select class="selectfilter" id="channelFilter"><option value="">판매처 전체</option></select>
          <button type="button" class="btn ghost" id="bulkEditBtn" title="왼쪽 체크박스로 요청 1건을 고른 뒤 누르면 그 요청을 수정해요" disabled style="opacity:.5;cursor:not-allowed;border-color:var(--accent);color:var(--accent);">
            ✏️ 선택 수정 <span id="bulkEditCount" style="font-variant-numeric:tabular-nums;">0</span>
          </button>
          <button type="button" class="btn ghost" id="bulkCopyBtn" title="왼쪽 체크박스로 요청 1건을 고른 뒤 누르면 그 내용 그대로 새 요청서를 만들어요 (정기 발주처럼 주소는 같고 상품만 바뀔 때 편해요)" disabled style="opacity:.5;cursor:not-allowed;border-color:var(--accent);color:var(--accent);">
            📄 선택 복사 <span id="bulkCopyCount" style="font-variant-numeric:tabular-nums;">0</span>
          </button>
          <button type="button" class="btn ghost" id="bulkAdvanceBtn" title="고른 안전재고 요청을 다음 단계로 진행해요 (대기 → 진행중 → 완료)" disabled style="display:none;opacity:.5;cursor:not-allowed;border-color:var(--warn);color:var(--warn);">
            ⏭ 선택 진행 <span id="bulkAdvanceCount" style="font-variant-numeric:tabular-nums;">0</span>
          </button>
          <button type="button" class="btn ghost" id="bulkDeleteBtn" title="왼쪽 체크박스로 고른 요청들을 한 번에 삭제(취소)해요" disabled style="opacity:.5;cursor:not-allowed;border-color:var(--danger,#d9534f);color:var(--danger,#d9534f);">
            🗑 선택 삭제 <span id="bulkDelCount" style="font-variant-numeric:tabular-nums;">0</span>
          </button>
        </div>
        <div class="tablescroll" id="listView">
          <table class="reqtable">
            <thead>
              <tr>
                <th style="width:34px;"><input type="checkbox" id="selectAll" title="이 목록 전체 선택" style="width:15px;height:15px;accent-color:var(--accent);cursor:pointer;vertical-align:middle;"></th>
                <th>요청번호</th>
                <th class="col-extra">요청일자</th>
                <th>요청팀</th>
                <th>요청자</th>
                <th>유상/무상</th>
                <th class="col-extra">판매처</th>
                <th>상품</th>
                <th class="num">수량</th>
                <th class="num col-extra">가용재고</th>
                <th class="col-due"><span class="dueLbl">출고 희망일</span></th>
                <th class="col-recv">수령자</th>
                <th class="col-to col-extra">수령처</th>
                <th>상태</th>
                <th class="col-track">송장</th>
                <th class="col-extra">완료일시</th>
                <th class="col-memo">비고(메모)</th>
              </tr>
            </thead>
            <tbody id="tableBody"></tbody>
          </table>
          <div class="table-empty" id="tableEmpty" style="display:none;">표시할 요청이 없어요</div>
        </div>
        <div class="pager" id="pager" style="display:none;"></div>
        <div id="calendarView" style="display:none;"></div>
        <div id="kanbanView" style="display:none;"></div>
      </div>

      <div class="howto">
        <strong>사용법</strong> — 슬랙으로 들어온 출고 요청은 <strong>요청서 작성</strong>에서 직접 입력하거나, 내용을 채팅창에 붙여넣어 주세요.
        요청서로 등록한 건은 이 브라우저에 저장되고 <span class="local-tag">로컬</span> 표시가 붙어요. 대장부 본문에 고정으로 남기려면 채팅으로 말씀해 주세요.
        판매처는 옵티마 약국 · 오프뷰티 · 올리브영 · 해외 · 기타 중에서 여러 곳을 함께 체크할 수 있고, 기타를 고르면 판매처 이름을 적는 칸이 열려요.
        표의 행을 클릭하면 상품 목록·수령 정보·요청내용 전체를 볼 수 있고, <strong>슬랙용 텍스트 복사</strong>로 그대로 회신할 수 있어요.
        상품·수량·출고 희망일 중 빠진 정보가 있으면 <span class="missing-tag">정보 확인 필요</span> 표시가 자동으로 붙어요.
      </div>

      <div class="logisticswrap" id="logisticsSection" style="display:none;">
        <div style="display:flex; flex-direction:column; align-items:center; justify-content:center;
                    gap:16px; padding:64px 24px; text-align:center;
                    background:var(--surface); border:1px solid var(--border); border-radius:12px;">
          <div style="width:56px; height:56px; border-radius:14px; background:var(--accent-soft, #E8EEFC);
                      display:flex; align-items:center; justify-content:center;">
            <svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="var(--accent)" stroke-width="2">
              <path d="M3 7l2-3h14l2 3"/><path d="M3 7h18v12a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V7Z"/><path d="M8 11h8"/>
            </svg>
          </div>
          <div>
            <div style="font-size:16px; font-weight:700; color:var(--text); margin-bottom:6px;">물류 대장은 별도 화면에서 열려요</div>
            <div style="font-size:13px; color:var(--text-muted); max-width:420px; line-height:1.6;">
              재고 확인·D-day 분류·보류 처리까지 되는 물류팀 전용 화면이에요.<br>
              보안 정책상 이 페이지 안에는 표시할 수 없어 새 창으로 열려요.
            </div>
          </div>
          <a href="https://myorder-logistics-dev-production.up.railway.app" target="_blank" rel="noopener"
             style="display:inline-flex; align-items:center; gap:8px; padding:10px 20px; border-radius:8px;
                    background:var(--accent); color:#fff; text-decoration:none; font-size:14px; font-weight:600;">
            물류 대장 새 창에서 열기
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14 5h5v5"/><path d="M19 5 10 14"/><path d="M8 5H6a1 1 0 0 0-1 1v12a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2"/>
            </svg>
          </a>
        </div>
      </div>

      <!-- ============ 엑셀 작업실 ============ -->
      <div class="exceltoolwrap" id="excelToolSection" style="display:none;">
        <div class="xt-toolbar" id="xtToolbar">
          <button type="button" class="xt-btn" id="xtUploadBtn">📁 파일 업로드</button>
          <input type="file" id="xtFileInput" accept=".xlsx,.xls,.csv" multiple style="display:none;">
          <span class="xt-sep"></span>
          <button type="button" class="xt-btn" id="xtVlookupBtn" disabled>🔗 VLOOKUP 매칭</button>
          <button type="button" class="xt-btn" id="xtStatBtn" disabled>Σ 합계·평균·개수</button>
          <button type="button" class="xt-btn" id="xtDedupeBtn" disabled>🧹 중복 제거</button>
          <button type="button" class="xt-btn" id="xtSortBtn" disabled>↕ 정렬</button>
          <button type="button" class="xt-btn" id="xtFilterBtn" disabled>🔍 필터</button>
          <button type="button" class="xt-btn" id="xtReplaceBtn" disabled>🔄 찾기·바꾸기</button>
          <button type="button" class="xt-btn" id="xtCalcColBtn" disabled>➕ 계산열 추가</button>
          <span class="xt-sep"></span>
          <button type="button" class="xt-btn" id="xtAddRowBtn" disabled title="행을 체크하고 누르면 그 행 바로 다음에 끼워 넣어요. 아무것도 안 골랐으면 맨 아래에 추가돼요.">⬇ 행 추가</button>
          <button type="button" class="xt-btn" id="xtDelRowBtn" disabled>🗑 선택 행 삭제</button>
          <button type="button" class="xt-btn" id="xtDelColBtn" disabled title="위쪽 열 문자(A, B, C…)를 클릭해서 파란색으로 고른 뒤 눌러요.">🗑 선택 열 삭제</button>
          <button type="button" class="xt-btn" id="xtUndoBtn" disabled>↩ 실행 취소</button>
          <span class="xt-spacer"></span>
          <label class="xt-headerlabel">헤더 행 <input type="number" id="xtHeaderRowInput" min="1" value="1"></label>
          <button type="button" class="xt-btn primary" id="xtDownloadBtn" disabled>⬇️ 다운로드</button>
        </div>
        <div class="xt-filetabs" id="xtFileTabs"></div>
        <div class="xt-status" id="xtStatus"></div>
        <div class="xt-gridwrap" id="xtGridWrap">
          <div class="xt-empty" id="xtEmpty">엑셀 파일(.xlsx/.xls/.csv)을 올리면 여기 표시돼요. 여러 파일을 올려서 서로 매칭(VLOOKUP)할 수도 있어요.</div>
          <div class="xt-tablescroll" id="xtTableScroll" style="display:none;">
            <table class="xt-table" id="xtTable"></table>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<script>
  // 물류팀 전용 대장 화면 (Railway 배포본, 새 탭으로 오픈)
  const LOGISTICS_DASHBOARD_URL = "https://myorder-logistics-dev-production.up.railway.app";
</script>

<script>
  // ===== 서버 연결 설정 =====
  const API_BASE = "https://logistics-backend-production-dabb.up.railway.app";
  const API_LEDGER = API_BASE + "/api/requests/ledger";
  const API_CREATE = API_BASE + "/api/requests";
  const API_STOCK  = API_BASE + "/api/stock";

  let SERVER = [];

  // 수정 모드 상태 — 이 값이 있으면 제출 시 새로 만들지 않고 이 요청을 업데이트합니다.
  let editingSrNo = null;

  // Slack 링크를 통해 들어온 경우, 나중에 송장 알림을 보낼 수 있도록 기억해 둡니다.
  const _urlParams = new URLSearchParams(window.location.search);
  const originSlackChannel = _urlParams.get("slack_channel") || null;
  const originSlackUser = _urlParams.get("slack_user") || null;
  let serverOk = true;
  let serverMsg = "";

  // 날짜 계산 기준. 비워 두면 실제 오늘 날짜를 씁니다.
  // 특정 날짜로 고정하려면 "2026-07-19" 처럼 넣으세요.
  const TODAY_OVERRIDE = "";

  function pad2(n) { return String(n).padStart(2, "0"); }
  function todayStr() { const d = new Date(); return `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())}`; }
  function nowStamp() { const d = new Date(); return `${todayStr()} ${pad2(d.getHours())}:${pad2(d.getMinutes())}`; }
  const TODAY = TODAY_OVERRIDE || todayStr();

  let LOCAL = [];
  let localStorageOk = true;
  function saveLocal() { return true; }

  function esc(s) {
    return String(s ?? "").replace(/[&<>"']/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
  }

  function fromServer(r) {
    let products = [];
    if (typeof r.productsJson === "string" && r.productsJson.trim()) {
      try { const p = JSON.parse(r.productsJson); if (Array.isArray(p)) products = p; }
      catch (e) { products = []; }
    } else if (Array.isArray(r.products)) {
      products = r.products;
    }

    let channels = Array.isArray(r.channels) ? r.channels.slice() : [];
    let channelOther = r.channelOther || "";
    channels = channels.map(c => {
      const s = String(c).trim();
      if (s.startsWith("기타:")) { channelOther = s.slice(3).trim(); return "기타"; }
      return s;
    }).filter(Boolean);

    const content = r.content || "";
    const note = (r.note && r.note !== content) ? r.note : "";

    return Object.assign({}, r, { products, channels, channelOther, content, note });
  }

  function normalize(r, source) {
    const products = Array.isArray(r.products) && r.products.length
      ? r.products.map(p => ({ code: p.code || "", name: p.name || "", option: p.option || "", qty: (p.qty ?? p.quantity ?? null), lineStatus: p.lineStatus || "", lineReason: p.lineReason || "" }))
      : (r.item || r.quantity != null ? [{ code: "", name: r.item || "", option: "", qty: r.quantity ?? null }] : []);
    const rc = r.receiver || {};
    return {
      requestId: r.requestId || "",
      receivedAt: r.receivedAt || "",
      requestDate: r.requestDate || (r.receivedAt || "").slice(0, 10),
      team: r.team || "",
      requester: r.requester || "",
      assignee: r.assignee || "",
      content: r.content || "",
      channels: Array.isArray(r.channels) ? r.channels.filter(Boolean) : [],
      channelOther: r.channelOther || "",
      products,
      receiver: {
        name: rc.name || "",
        phone: rc.phone || "",
        address: rc.address || r.destination || "",
        message: rc.message || "",
      },
      desiredDate: r.desiredDate || "",
      billing: r.billing || "",
      note: r.note || "",
      status: r.status || "pending",
      completedAt: r.completedAt || "",
      carrier: r.carrier || "",
      trackingNo: r.trackingNo || "",
      trackingNotified: !!r.trackingNotified,
      requestType: r.requestType || "출고요청",
      memo: r.memo || "",              // 물류팀 특이사항 메모(진행상황 모니터링용)
      source: source,
    };
  }

  // 오프뷰티 매장 주소 자동완성 데이터 (매장명 입력 시 주소·연락처 자동 채움)
  const OFFBEAUTY_STORES = [{"name":"오프뷰티 광장시장점","addr":"서울 종로구 창경궁로 88 1층 252호","phone":"010-9936-4550"},{"name":"오프뷰티 망원점","addr":"서울 마포구 월드컵로 13길 23 1층","phone":"010-3194-4868"},{"name":"오프뷰티 인사점","addr":"서울 종로구 인사동길 14 1층","phone":"010-5103-5338"},{"name":"오프뷰티 고대점","addr":"서울 성북구 안암동5가 104-30번지 1층","phone":"010-8407-5909"},{"name":"오프뷰티 이대점","addr":"서울 서대문구 이화여대길 51 1층","phone":"010-3320-0959"},{"name":"오프뷰티 부산괴정역점","addr":"부산 사하구 사하로 200(형지스퀘어) 1층","phone":"010-8181-9905"},{"name":"오프뷰티 대전중앙시장점","addr":"대전광역시 동구 원동 49-6 1층","phone":"010-7924-0879"},{"name":"오프뷰티 천안 펜타포트점","addr":"충남 천안시 서북구 공원로 196 펜타포트 지하2층","phone":"010-2815-5175"},{"name":"오프뷰티 구리남양주점","addr":"경기도 구리시 경춘북로 230 모다아울렛 1층","phone":"010-4024-9459"},{"name":"오프뷰티 춘천점","addr":"강원 춘천시 방송길 77 모다아울렛 1층","phone":"010-3538-6392"},{"name":"오프뷰티 인천부평점","addr":"인천광역시 부평구 부평동 70-127 지하 1층","phone":"010-7924-7122"},{"name":"오프뷰티 시흥점","addr":"경기 시흥시 장현능곡로 155, 플랑드르 모다아울렛 3층 오프뷰티 시흥점","phone":"010-8809-1025"},{"name":"오프뷰티 스타필드 고양점","addr":"경기 고양시 덕양구 고양대로 1955 스타필드 고양점 1층 신세계팩토리 오프뷰티","phone":"010-5299-7214"},{"name":"오프뷰티 부산 서면점","addr":"부산광역시 부산진구 중앙대로692번길 22 (부전동), 1층","phone":"010-6575-9835"},{"name":"오프뷰티 청주성안점","addr":"충북 청주시 상당구 상당로69번길 20 (북문로 1가), 1층","phone":"010-5606-8132"},{"name":"오프뷰티 전주객사점","addr":"전북 전주시 완산구 전주객사5길 21-13  (GS편의점, 올리브영 사이 건물)","phone":"010-8648-0376"},{"name":"오프뷰티 광주 충장로점","addr":"광주광역시 동구 충장로 83-1 1층, 오프뷰티","phone":"010-9257-0556"},{"name":"오프뷰티 수원역점","addr":"수원 매산로 1가 11-14, 1층","phone":"010-7486-3008"},{"name":"오프뷰티 창원 상남점","addr":"경상남도 창원시 성산구 상남동 11-7, 1층","phone":"010-8524-0382"},{"name":"오프뷰티 부산 남포점","addr":"부산시 중구 광복로 52 1층","phone":"010-8696-9825"},{"name":"오프뷰티 거제 디큐브점","addr":"경상남도 거제시 장평로 12 디큐브백화점 1층","phone":"010-4646-9434"},{"name":"오프뷰티 강남점","addr":"서울특별시 강남구 봉은사로 315 (논현동, 재인빌딩) 1층","phone":"010-8946-4010"},{"name":"오프뷰티 대구동성로점","addr":"대구광역시 중구 동성로 8-1 (동성로3가), 1층","phone":"010-2886-2419"},{"name":"오프뷰티 명동스퀘어점","addr":"서울특별시 중구 명동8나길 14 (충무로1가)","phone":"010-3917-0537"},{"name":"오프뷰티 강릉중앙점","addr":"강릉시 경강로 2090-1, 1층","phone":"010-9214-2068"},{"name":"오프뷰티 경동시장점","addr":"서울 동대문구 고산자로 451 세우약령시빌딩 1층","phone":"010-4680-5121"},{"name":"오프뷰티 성수메가팩토리점","addr":"서울특별시 성동구 연무장3길 9 (성수동2가) 나동 1층","phone":"010-4787-3586"},{"name":"오프뷰티 신촌점","addr":"서울특별시 서대문구 연세로 31 1층","phone":"010-4202-7280"},{"name":"오프뷰티 제주연동점","addr":"제주특별자치도 제주시 연동7길 26(연동) 1층","phone":"010-8834-3598"},{"name":"오프뷰티 신림점","addr":"서울 관악구 남부순환로 1607 1층","phone":"010-4083-2389"},{"name":"오프뷰티 남대문점","addr":"서울 중구 남대문시장길 17 1층","phone":"010-5176-2176"},{"name":"오프뷰티 제주동문시장점","addr":"제주특별자치도 제주시 동문로 6-1 (일도일동, 보성빌딩), 1층","phone":"010-8542-0365"},{"name":"오프뷰티 행복한백화점","addr":"서울 양천구 목동동로 309, 1층","phone":"010-7221-3319"},{"name":"오프뷰티 경주중앙점","addr":"경상북도 경주시 동성로 112(노동동), 1층","phone":"010-4495-0234"},{"name":"오프뷰티 스타필드 운정점","addr":"경기 파주시 와석순환로515번길 70, D동 4107호(1층)","phone":"010-9396-0735"},{"name":"오프뷰티 청계광장시장점","addr":"서울 종로구 청계천로 185, 1층","phone":"010-8342-4573"},{"name":"오프뷰티 익선점","addr":"서울 종로구 수표로28길 21-15","phone":""},{"name":"오프뷰티 부평역점","addr":"인천광역시 부평구 부평문화로66번길 23, 1층","phone":"010-8938-4021"},{"name":"오프뷰티 평택신장점","addr":"경기 평택시 쇼핑로 37, 1층","phone":"010-3355-0183"},{"name":"오프뷰티 롯데프리미엄아울렛 이천점","addr":"경기 이천시 호법면 프리미엄아울렛로 177-74  롯데프리미엄아울렛이천점 키즈몰 A 블럭 3층","phone":"010-3649-8278"}];

  let DATA = [];
  function rebuildData() {
    DATA = SERVER.map(r => normalize(fromServer(r), "server"));
  }

  async function fetchFromServer() {
    try {
      const res = await fetch(API_LEDGER, { headers: { "Accept": "application/json" } });
      if (!res.ok) throw new Error("서버 응답 " + res.status);
      const json = await res.json();
      SERVER = Array.isArray(json) ? json : [];
      serverOk = true;
      serverMsg = "";
    } catch (e) {
      SERVER = [];
      serverOk = false;
      serverMsg = e.message || "연결 실패";
    }
    refreshAll();
    paintServerState();
    loadStockBadges();
  }

  function paintServerState() {
    const el = document.getElementById("tableEmpty");
    if (!el) return;
    if (!serverOk) {
      el.innerHTML = '<div style="padding:24px 0;text-align:center;line-height:1.7;">'
        + '<strong>서버에 연결하지 못했어요.</strong><br>'
        + '<span style="opacity:.75;font-size:13px;">' + esc(serverMsg) + '</span><br>'
        + '<span style="opacity:.75;font-size:13px;">잠시 후 새로 고침해 주세요. 계속되면 담당자에게 알려주세요.</span>'
        + '</div>';
    } else if (!DATA.length) {
      el.innerHTML = '<div style="padding:24px 0;text-align:center;line-height:1.7;">'
        + '아직 등록된 요청이 없어요.<br>'
        + '<span style="opacity:.75;font-size:13px;">왼쪽 메뉴의 &lsquo;요청서 작성&rsquo;에서 첫 요청을 등록해 보세요.</span>'
        + '</div>';
    }
  }

  // ===== 이지어드민 가용재고 표시 =====
  // 상품코드(SKU)가 입력된 상품만 조회됩니다. 코드가 없으면 표시를 건너뜁니다.
  let STOCK = {};
  async function loadStockBadges() {
    const codes = [...new Set(
      DATA.flatMap(r => r.products.map(p => (p.code || "").trim()).filter(Boolean))
    )];
    if (!codes.length) return;
    try {
      const res = await fetch(API_STOCK + "?codes=" + encodeURIComponent(codes.join(",")));
      if (!res.ok) return;
      STOCK = await res.json();
      document.querySelectorAll("[data-stock-code]").forEach(el => {
        const code = el.getAttribute("data-stock-code");
        if (!code) return; // 상품코드 없는 행은 "-" 그대로 둠
        if (el.hasAttribute("data-req-qty")) { paintLineStock(el); return; }
        const s = STOCK[code];
        el.textContent = s
          ? (s.available != null ? `가용 ${s.available}` : `재고 ${s.stock}`)
          : "코드 없음";
      });
      refreshLineStocks();   // 상세표 가용재고(상품코드·바코드) 실시간 갱신
    } catch (e) { /* 재고 표시는 부가 정보이므로 실패해도 화면은 정상 동작 */ }
  }

  function totalQty(r) {
    const nums = r.products.map(p => Number(p.qty)).filter(n => Number.isFinite(n) && n > 0);
    return nums.length ? nums.reduce((a, b) => a + b, 0) : null;
  }
  function productSummary(r) {
    if (!r.products.length || !r.products[0].name) return "";
    const first = r.products[0].name;
    return r.products.length > 1 ? `${first} 외 ${r.products.length - 1}건` : first;
  }

  // 상품코드(SKU)가 입력된 첫 상품의 가용재고를 작은 배지로 보여줍니다.
  // 코드가 없거나 이지어드민에 없는 상품이면 배지를 표시하지 않습니다(오류 아님).
  // 가용재고 칸에 들어갈 초기 텍스트. 상품코드가 없으면 "-", 있는데 아직 조회 전이면 빈 칸으로 두고
  // 이후 loadStockBadges()/반복 갱신에서 채웁니다.
  function stockCellText(r) {
    const code = (r.products[0] && r.products[0].code || "").trim();
    if (!code) return "-";
    const s = STOCK[code];
    if (!s) return "";
    return s.available != null ? `가용 ${s.available}` : `재고 ${s.stock}`;
  }

  // 상세보기 상품표의 '가용재고' 칸 전용 캐시.
  // 상품코드(S코드)든 바코드(88…)든 검색 API로 조회한 값을 code 기준으로 담아둡니다.
  let LINE_STOCK = {};

  // 상세보기 상품표의 '가용재고' 칸 그리기(LINE_STOCK에서 읽음). 요청 수량보다 가용재고가
  // 적으면 과발주 방지를 위해 빨간색 ⚠️ 경고를 함께 표시합니다.
  function paintLineStock(el) {
    const code = el.getAttribute("data-stock-code");
    const req = Number(el.getAttribute("data-req-qty"));
    const s = code ? LINE_STOCK[code] : null;
    if (!s) { el.className = "num stockline"; el.innerHTML = '<span class="v empty">조회 중…</span>'; return; }
    const avail = (s.available != null ? s.available : s.stock);
    if (avail == null) { el.className = "num stockline"; el.innerHTML = '<span class="v empty">-</span>'; return; }
    const under = Number.isFinite(req) && req > 0 && avail < req;
    el.className = "num stockline " + (under ? "stock-over" : "stock-ok");
    el.title = under
      ? `요청 ${req} > 가용 ${avail} — 재고가 부족해요 (과발주 주의)`
      : `현재 가용재고 ${avail}`;
    el.innerHTML = `가용 <b>${avail}</b>${under ? ' <span class="stock-warn">⚠️ 부족</span>' : ''}`;
  }

  // 화면에 보이는 상세표의 가용재고 칸을 실시간으로 갱신합니다.
  // 상품코드·바코드 모두 검색 API로 조회되므로 둘 다 정확히 잡힙니다.
  async function refreshLineStocks() {
    const cells = [...document.querySelectorAll('[data-req-qty][data-stock-code]')].filter(el => el.offsetParent !== null);
    if (!cells.length) return;
    const codes = [...new Set(cells.map(el => el.getAttribute('data-stock-code')).filter(Boolean))];
    await Promise.all(codes.map(async (c) => {
      try {
        const r = await fetch(API_STOCK + "/search?q=" + encodeURIComponent(c) + "&withStock=true");
        if (!r.ok) return;
        const list = await r.json();
        if (Array.isArray(list) && list.length) {
          // S코드면 productId 정확 일치를 우선, 바코드면 유일 결과이므로 첫 항목
          const hit = list.find(x => String(x.productId || "") === c) || list[0];
          LINE_STOCK[c] = { available: hit.available, stock: hit.stock };
        }
      } catch (e) { /* 재고는 부가정보 — 실패해도 화면은 정상 */ }
    }));
    cells.forEach(paintLineStock);
  }
  function daysUntil(dateStr) {
    if (!dateStr) return null;
    const today = new Date(TODAY + "T00:00:00");
    const due = new Date(dateStr.slice(0, 10) + "T00:00:00");
    return Math.round((due - today) / 86400000);
  }
  // 취소 건은 처리할 게 없으므로 지연/임박 집계에서 제외합니다.
  function isActive(r) { return r.status !== "cancelled"; }
  function isOverdue(r) { const d = daysUntil(r.desiredDate); return r.status !== "done" && r.status !== "cancelled" && d !== null && d < 0; }
  function isSoon(r) { const d = daysUntil(r.desiredDate); return r.status !== "done" && r.status !== "cancelled" && d !== null && d >= 0 && d <= 1; }
  function hasMissingInfo(r) { return !productSummary(r) || !totalQty(r) || !r.desiredDate; }
  function fmtDue(r) {
    if (!r.desiredDate) return "-";
    const d = daysUntil(r.desiredDate);
    if (r.status === "done") return r.desiredDate;
    if (d < 0) return `${r.desiredDate} (${Math.abs(d)}일 지남)`;
    if (d === 0) return `${r.desiredDate} (오늘)`;
    if (d === 1) return `${r.desiredDate} (내일)`;
    return r.desiredDate;
  }
  const STATUS_LABEL = { pending: "대기", in_progress: "진행중", done: "완료", cancelled: "취소" };
  const BILLING_LABEL = { paid: "유상", free: "무상" };
  // 안전재고 상태 전환용: 화면 상태(영문) → 서버가 받는 한글 상태 / 다음 단계 안내
  const STATUS_KO = { pending: "대기", in_progress: "진행중", done: "완료" };
  const SAFETY_NEXT = { pending: "진행중", in_progress: "완료", done: "대기" };       // 눌렀을 때 넘어갈 상태(한글)
  const SAFETY_NEXT_LABEL = { pending: "진행중으로", in_progress: "완료로", done: "대기로" }; // 버튼에 보일 글자
  const CHANNELS = ["옵티마 약국", "오프뷰티", "올리브영", "해외", "기타"];

  function channelText(r) {
    if (!r.channels.length) return "";
    return r.channels.map(c => (c === "기타" && r.channelOther) ? `기타(${r.channelOther})` : c).join(", ");
  }

  // "" = 전체 / "open" = 대기+진행중(진행 필요) / "pending" / "in_progress" / "done"
  // 기본값은 '진행 필요' — 완료건이 계속 쌓여 목록을 덮는 문제를 없앴습니다. (팀장님 피드백 ①)
  let activeStatus = "open";
  let activeTeam = null;
  let activeAssignee = null;
  // 상세 검색(기간·요청자) — 전체 요청·안전재고 화면에 공통으로 적용됩니다.
  let activeRequester = null;
  let dateRangeField = "desiredDate";
  let dateRangeFrom = "";
  let dateRangeTo = "";
  let activeChannel = null;
  let activeView = "dashboard";
  let searchTerm = "";
  let todayOnly = false;
  let viewMode = "calendar";    // 기본을 캘린더로 (list | calendar | kanban 전환)
  let PAGE = 1;                 // 목록 보기의 현재 페이지 (1부터)
  let PAGE_SIZE = 30;           // 한 페이지에 보여줄 요청 건수
  let _pageSig = "";            // 필터가 바뀌면 1페이지로 되돌리기 위한 비교값
  let calCur = null;            // 캘린더가 보고 있는 달 {y, m}
  let calByDate = {};           // 캘린더에 지금 표시된 날짜별 요청 목록(필터 반영) — '+N건 더' 팝업이 사용
  const expanded = new Set();
  const selected = new Set();   // 엑셀로 뽑을 주문건(요청번호) 선택 목록

  // 선택 상태 UI(전체선택 체크, 선택 개수, '선택 엑셀' 버튼 활성화) 갱신
  function updateSelectionUI() {
    const visibleIds = [...document.querySelectorAll("#tableBody tr.mainrow")].map(tr => tr.dataset.id);
    const selCountEl = document.getElementById("selCount");
    const btn = document.getElementById("exportSelectedBtn");
    if (selCountEl) selCountEl.textContent = selected.size;
    if (btn) {
      const none = selected.size === 0;
      btn.disabled = none;
      btn.style.opacity = none ? ".5" : "1";
      btn.style.cursor = none ? "not-allowed" : "pointer";
    }
    // '선택 삭제' 버튼도 선택 개수에 맞춰 켜고 끕니다
    const delBtn = document.getElementById("bulkDeleteBtn");
    const delCountEl = document.getElementById("bulkDelCount");
    if (delCountEl) delCountEl.textContent = selected.size;
    if (delBtn) {
      const none = selected.size === 0;
      delBtn.disabled = none;
      delBtn.style.opacity = none ? ".5" : "1";
      delBtn.style.cursor = none ? "not-allowed" : "pointer";
    }
    // '선택 수정' 버튼 — 정확히 1건 골랐을 때만 활성화 (수정은 한 번에 한 건)
    const editBtn = document.getElementById("bulkEditBtn");
    const editCountEl = document.getElementById("bulkEditCount");
    if (editCountEl) editCountEl.textContent = selected.size;
    if (editBtn) {
      const one = selected.size === 1;
      editBtn.disabled = !one;
      editBtn.style.opacity = one ? "1" : ".5";
      editBtn.style.cursor = one ? "pointer" : "not-allowed";
    }
    // '선택 복사' 버튼 — 수정과 마찬가지로 정확히 1건 골랐을 때만 활성화
    const copyBtn = document.getElementById("bulkCopyBtn");
    const copyCountEl = document.getElementById("bulkCopyCount");
    if (copyCountEl) copyCountEl.textContent = selected.size;
    if (copyBtn) {
      const one = selected.size === 1;
      copyBtn.disabled = !one;
      copyBtn.style.opacity = one ? "1" : ".5";
      copyBtn.style.cursor = one ? "pointer" : "not-allowed";
    }
    // '선택 진행' 버튼(안전재고 전용) — 1건 이상 골랐을 때 활성화
    const advBtn = document.getElementById("bulkAdvanceBtn");
    const advCountEl = document.getElementById("bulkAdvanceCount");
    if (advCountEl) advCountEl.textContent = selected.size;
    if (advBtn) {
      const none = selected.size === 0;
      advBtn.disabled = none;
      advBtn.style.opacity = none ? ".5" : "1";
      advBtn.style.cursor = none ? "not-allowed" : "pointer";
    }
    const all = document.getElementById("selectAll");
    if (all) {
      const selectableVisible = visibleIds.length;
      const selectedVisible = visibleIds.filter(id => selected.has(id)).length;
      all.checked = selectableVisible > 0 && selectedVisible === selectableVisible;
      all.indeterminate = selectedVisible > 0 && selectedVisible < selectableVisible;
    }
  }

  // 안전재고·취소 내역처럼 지표 카드를 아예 안 쓰는 화면에서는, 다른 이유로 focus mode가
  // 꺼지더라도(예: 상태 탭을 눌러서 clearTodayFilter가 불릴 때) 카드가 다시 튀어나오면 안 됩니다.
  function chromeHiddenView() {
    return activeView === "form" || activeView === "logistics" || activeView === "cancelled" || activeView === "safety" || activeView === "exceltool";
  }
  function setFocusMode(on) {
    const hide = on || chromeHiddenView();
    document.querySelector(".stats").style.display = hide ? "none" : "";
    document.querySelector(".panels").style.display = hide ? "none" : "";
    document.querySelector(".howto").style.display = hide ? "none" : "";
  }

  function clearTodayFilter() {
    todayOnly = false;
    document.getElementById("clearTodayFilter").style.display = "none";
    setFocusMode(false);
  }

  function computeCounts() {
    // 대시보드 지표는 출고요청만 집계 (취소·안전재고 제외)
    const base = DATA.filter(r => r.status !== "cancelled" && r.requestType !== "안전재고");
    const total = base.length;
    const done = base.filter(r => r.status === "done").length;
    const progress = base.filter(r => r.status === "in_progress").length;
    const pending = base.filter(r => r.status === "pending").length;
    const cancelled = DATA.filter(r => r.status === "cancelled").length;
    // 안전재고 배지는 '확인이 필요한' 건만 셉니다 — 완료된 건은 더 볼 필요가 없어서 제외
    const safety = DATA.filter(r => r.requestType === "안전재고" && r.status !== "cancelled" && r.status !== "done").length;
    const overdue = base.filter(isOverdue).length;
    const missing = base.filter(r => r.status !== "done" && hasMissingInfo(r)).length;
    return { total, done, progress, pending, cancelled, safety, overdue, missing };
  }

  function renderStats() {
    const c = computeCounts();
    const pct = (n) => c.total ? Math.round((n / c.total) * 100) : 0;
    document.getElementById("statTotal").innerHTML = c.total + '<span class="unit">건</span>';
    document.getElementById("statDone").innerHTML = c.done + '<span class="unit">건</span>';
    document.getElementById("statDonePct").textContent = pct(c.done) + "%";
    document.getElementById("statProgress").innerHTML = c.progress + '<span class="unit">건</span>';
    document.getElementById("statProgressPct").textContent = pct(c.progress) + "%";
    document.getElementById("statPending").innerHTML = c.pending + '<span class="unit">건</span>';
    document.getElementById("statPendingPct").textContent = pct(c.pending) + "%";
    document.getElementById("statOverdue").innerHTML = c.overdue + '<span class="unit">건</span>';
    document.getElementById("statMissing").innerHTML = c.missing + '<span class="unit">건</span>';

    // 0건인 경고 카드(기한 초과·정보 누락)는 어느 화면에서든 감춥니다.
    // — "확인할 필요 없거나 활성화되지 않는 건 가리자"는 피드백 반영.
    //   (카드 하나만 둘째 줄에 덜렁 남는 것도 함께 없어집니다)
    const card = (cls) => document.querySelector(".stat." + cls);
    const toggleCard = (cls, n) => { const el = card(cls); if (el) el.style.display = n ? "" : "none"; };
    toggleCard("overdue", c.overdue);
    toggleCard("missing", c.missing);

    document.getElementById("bellCount").textContent = c.overdue;
    document.getElementById("navAllBadge").textContent = c.overdue > 0 ? c.overdue : "";
    document.getElementById("navAllBadge").style.display = c.overdue > 0 ? "" : "none";
    const safetyBadge = document.getElementById("navSafetyBadge");
    if (safetyBadge) {
      safetyBadge.textContent = c.safety > 0 ? c.safety : "";
      safetyBadge.style.display = c.safety > 0 ? "" : "none";
    }

    // '업무 진행률' 도넛은 화면에서 없앴으므로 그리지 않습니다. (2026-08-27)
  }

  function renderToday() {
    const list = DATA.filter(r => isActive(r) && r.requestType !== "안전재고" && r.status !== "done" && r.desiredDate === TODAY)
      .sort((a, b) => (a.requestId || "").localeCompare(b.requestId || ""));
    document.getElementById("todayCount").textContent = list.length;
    const el = document.getElementById("todayList");
    if (!list.length) {
      el.innerHTML = '<div class="panel-empty">오늘 처리할 업무가 없어요</div>';
      return;
    }
    el.innerHTML = list.slice(0, 8).map(r => `
      <div class="urgentrow">
        <input type="checkbox" disabled ${r.status === "done" ? "checked" : ""}>
        <span class="title">${esc(productSummary(r)) || '<span style="color:var(--text-muted)">상품 미확인</span>'} <span style="color:var(--text-muted)">(${esc(r.requestId)})</span></span>
        <span class="who">${esc(r.assignee || r.requester)}</span>
        <span class="statuspill ${r.status}">${STATUS_LABEL[r.status]}</span>
      </div>`).join("");
  }

  /** 요청팀 필터 — 팀이 늘어나 화면이 좌우로 길어지던 칩 목록을 드롭다운으로 바꿨습니다.
   *  (팀장님 피드백 ①) 이름은 그대로 둬서 기존 호출부를 건드리지 않아요. */
  function renderTeamChips() {
    const el = document.getElementById("teamFilter");
    if (!el) return;
    const teams = [...new Set(DATA.map(r => r.team).filter(Boolean))].sort((a, b) => a.localeCompare(b, "ko"));
    el.innerHTML = '<option value="">전체 팀</option>'
      + teams.map(t => `<option value="${esc(t)}">${esc(t)}</option>`).join("");
    el.value = teams.includes(activeTeam) ? activeTeam : "";
    if (el.value === "") activeTeam = null;
  }

  /** 상태 탭의 건수 배지 — 지금 화면(팀·담당자·판매처·검색 필터까지 반영)에서 대기/진행중이 몇 건인지 보여줍니다. */
  function renderStatusTabCounts() {
    // 상태 조건만 제외한 나머지 필터를 그대로 적용해서 셉니다.
    const keep = activeStatus;
    activeStatus = "";
    let rows;
    try { rows = getFilteredRows(); } finally { activeStatus = keep; }
    const pending = rows.filter(r => r.status === "pending").length;
    const progress = rows.filter(r => r.status === "in_progress").length;
    const set = (id, n, hideWhenZero) => {
      const el = document.getElementById(id);
      if (!el) return;
      el.textContent = n;
      el.style.display = (hideWhenZero && !n) ? "none" : "";
    };
    set("tabOpenCount", pending + progress, false);
    set("tabPendingCount", pending, false);
    set("tabProgressCount", progress, false);
  }

  function renderAssigneeFilter() {
    const assignees = [...new Set(DATA.map(r => r.assignee).filter(Boolean))];
    const el = document.getElementById("assigneeFilter");
    el.innerHTML = '<option value="">담당자 전체</option>' + assignees.map(a => `<option value="${esc(a)}">${esc(a)}</option>`).join("");
    el.value = assignees.includes(activeAssignee) ? activeAssignee : "";
    if (el.value === "") activeAssignee = null;
  }

  /** 상세 검색의 '요청자' 드롭다운 — 실제 들어온 요청자 이름만 골라 보여줍니다. */
  function renderRequesterFilter() {
    const requesters = [...new Set(DATA.map(r => r.requester).filter(Boolean))].sort((a, b) => a.localeCompare(b, "ko"));
    const el = document.getElementById("spRequester");
    if (!el) return;
    el.innerHTML = '<option value="">전체</option>' + requesters.map(a => `<option value="${esc(a)}">${esc(a)}</option>`).join("");
    el.value = requesters.includes(activeRequester) ? activeRequester : "";
    if (el.value === "") activeRequester = null;
  }

  function renderChannelFilter() {
    const used = CHANNELS.filter(c => DATA.some(r => r.channels.includes(c)));
    const el = document.getElementById("channelFilter");
    el.innerHTML = '<option value="">판매처 전체</option>' + used.map(c => `<option value="${esc(c)}">${esc(c)}</option>`).join("");
    el.value = used.includes(activeChannel) ? activeChannel : "";
    if (el.value === "") activeChannel = null;
  }

  // 수령자 이름 → 가장 최근에 썼던 연락처/주소/메세지 (자동완성용)
  let RECEIVER_HISTORY = {};

  function renderDatalists() {
    const fill = (id, values) => {
      document.getElementById(id).innerHTML = [...new Set(values.filter(Boolean))]
        .map(v => `<option value="${esc(v)}"></option>`).join("");
    };
    fill("teamOptions", DATA.map(r => r.team));
    fill("requesterOptions", DATA.map(r => r.requester));
    fill("assigneeOptions", DATA.map(r => r.assignee));
    fill("receiverOptions", [...DATA.map(r => r.receiver.name), ...OFFBEAUTY_STORES.map(s => s.name)]);

    // 접수일시가 최신인 순서로 정리해서, 이름별 "가장 최근" 수령정보만 남깁니다.
    RECEIVER_HISTORY = {};
    DATA.slice()
      .sort((a, b) => (a.receivedAt || "").localeCompare(b.receivedAt || ""))
      .forEach(r => {
        const name = (r.receiver.name || "").trim();
        if (!name) return;
        RECEIVER_HISTORY[name] = {
          phone: r.receiver.phone || "",
          address: r.receiver.address || "",
          message: r.receiver.message || "",
        };
      });
  }

  function productTableHtml(r) {
    if (!r.products.length) {
      return '<div class="drow"><div class="v empty" style="grid-column:1 / -1;">등록된 상품이 없어요 — 요청자에게 상품·수량을 확인해 주세요.</div></div>';
    }
    const sum = totalQty(r);
    const anyStatus = r.products.some(p => p.lineStatus);
    const statusPill = (p) => {
      if (!p.lineStatus) return '<span class="v empty">-</span>';
      const cls = p.lineStatus === "반려" ? "ls-reject" : (p.lineStatus === "교환요청" ? "ls-exch" : "ls-hold");
      const reason = p.lineReason ? ` title="${esc(p.lineReason)}"` : "";
      return `<span class="lspill ${cls}"${reason}>${esc(p.lineStatus)}</span>`;
    };
    return `<table class="ptable">
      <thead><tr><th class="num">#</th><th>상품코드</th><th>상품명</th><th>옵션</th><th class="num">요청 수량</th><th class="num">가용재고</th>${anyStatus ? "<th>상품 상태</th>" : ""}</tr></thead>
      <tbody>${r.products.map((p, i) => `<tr>
        <td class="num">${i + 1}</td>
        <td class="num">${esc(p.code) || "-"}</td>
        <td>${esc(p.name) || "-"}</td>
        <td>${esc(p.option) || "-"}</td>
        <td class="num">${p.qty ?? "-"}</td>
        <td class="num stockline"${p.code ? ` data-stock-code="${esc(p.code)}" data-req-qty="${p.qty ?? ""}"` : ""}>${p.code ? '<span class="v empty">조회 중…</span>' : '<span class="v empty">코드 없음</span>'}</td>
        ${anyStatus ? `<td>${statusPill(p)}${p.lineReason ? ` <span class="lsreason">${esc(p.lineReason)}</span>` : ""}</td>` : ""}
      </tr>`).join("")}</tbody>
      <tfoot><tr><td colspan="4">합계 ${r.products.length}종</td><td class="num">${sum ?? "-"}</td><td></td>${anyStatus ? "<td></td>" : ""}</tr></tfoot>
    </table>`;
  }

  function detailHtml(r) {
    const v = (s) => s ? esc(s) : '<span class="v empty">-</span>';
    const num = (s) => s ? `<span class="num">${esc(s)}</span>` : '<span class="v empty">-</span>';
    const drow = (k, val) => `<div class="drow"><div class="k">${k}</div><div class="v">${val}</div></div>`;
    const safety = r.requestType === "안전재고";  // 안전재고는 수령·송장·판매처·유상무상 표시 안 함
    return `<div class="detailbox">
      <div class="dhead">
        <span class="dtitle">${esc(r.requestId)} 상세${safety ? ' · <span style="color:var(--info);font-weight:700;">안전재고</span>' : ''}</span>
        <span class="spacer">
          <button type="button" class="btn subtle small" data-copy="${esc(r.requestId)}">슬랙용 텍스트 복사</button>
          ${r.source === "local" ? `<button type="button" class="btn danger small" data-del="${esc(r.requestId)}">삭제</button>` : ""}
        </span>
      </div>

      <div class="detailgrid" data-detailgrid="${esc(r.requestId)}">
      <div>

      <div class="dcard pad">
        <div class="dcard-title">📦 상품 정보</div>
        ${productTableHtml(r)}
      </div>

      ${(safety || r.memo) ? `<div class="dcard pad">
        <div class="dcard-title">🗒 물류 메모 (특이사항)
          <button type="button" class="btn ghost small" data-memo="${esc(r.requestId)}" style="margin-left:6px;">메모 편집</button>
        </div>
        <div class="v" style="white-space:pre-wrap;line-height:1.55;">${r.memo ? esc(r.memo) : '<span class="v empty">아직 메모가 없어요 — 관리 열의 📝 메모 버튼이나 위 버튼으로 특이사항을 적어두면 진행상황을 함께 볼 수 있어요.</span>'}</div>
      </div>` : ""}

      ${safety ? "" : `<div class="dcard pad">
        <div class="dcard-title">🚚 수령 정보</div>
        ${drow("수령자", v(r.receiver.name))}
        ${drow("연락처", num(r.receiver.phone))}
        ${drow("주소", v(r.receiver.address))}
        ${drow("배송 메세지", v(r.receiver.message))}
      </div>`}

      <div class="dcard pad">
        <div class="dcard-title">📋 요청 정보</div>
        ${safety ? "" : drow("판매처", r.channels.length ? esc(channelText(r)) : '<span class="v empty">미지정</span>')}
        ${drow("요청일자", num(r.requestDate))}
        ${drow("접수일시", num(r.receivedAt))}
        ${safety ? "" : drow("유상 / 무상", r.billing ? BILLING_LABEL[r.billing] : '<span class="v empty">미지정</span>')}
        ${drow("완료일시", num(r.completedAt))}
        ${drow("요청내용", v(r.content))}
        ${r.note ? drow("비고", esc(r.note)) : ""}
      </div>

      ${(r.status === "cancelled" || safety) ? "" : `<div class="dcard pad">
        <div class="dcard-title">🧾 송장 정보</div>
        ${trackingBoxHtml(r)}
      </div>`}

      <div class="dcard pad">
        <div class="dcard-title">📎 첨부파일</div>
        <div class="attach-list" data-attach-list="${esc(r.requestId)}"><span class="v empty" style="font-size:13px;color:var(--text-muted);">불러오는 중…</span></div>
      </div>

      <div class="dcard pad">
        <div class="dcard-title">🕘 변경 이력</div>
        <div class="history-list" data-history-list="${esc(r.requestId)}"><span class="v empty" style="font-size:13px;color:var(--text-muted);">불러오는 중…</span></div>
      </div>

      </div>
      <div>
        <div class="dcard pad srcprev" data-srcprev="${esc(r.requestId)}">
          <div class="dcard-title">📄 요청자가 올린 원본 파일</div>
          <div class="srcprev-body"><span class="v empty" style="font-size:13px;color:var(--text-muted);">불러오는 중…</span></div>
        </div>
      </div>
      </div>
    </div>`;
  }

  // 변경 이력에서 비교할 필드 라벨
  const HIST_FIELD_LABELS = { team:"요청팀", requester:"요청자", assignee:"담당자", itemName:"상품",
    optionValue:"옵션", quantity:"수량", wantDate:"희망일", receivePlace:"수령처", note:"요청내용",
    channels:"판매처", productsJson:"상품목록", receiverName:"수령자", receiverPhone:"연락처",
    receiverAddress:"주소", receiverMessage:"배송메세지", billingType:"구분", status:"상태" };

  function diffChangedFields(beforeJson, afterJson) {
    let b = {}, a = {};
    try { b = JSON.parse(beforeJson || "{}"); } catch (e) {}
    try { a = JSON.parse(afterJson || "{}"); } catch (e) {}
    const changed = [];
    Object.keys(HIST_FIELD_LABELS).forEach(k => {
      const bv = b[k] == null ? "" : String(b[k]);
      const av = a[k] == null ? "" : String(a[k]);
      if (bv !== av) changed.push(HIST_FIELD_LABELS[k]);
    });
    return changed;
  }

  function renderHistory(list) {
    if (!Array.isArray(list) || !list.length) {
      return `<span style="font-size:13px;color:var(--text-muted);">아직 변경 이력이 없어요.</span>`;
    }
    return list.map(h => {
      const isCancel = h.action === "취소";
      const badge = isCancel
        ? `<span style="background:var(--danger);color:#fff;border-radius:5px;padding:1px 7px;font-size:11px;font-weight:700;">취소</span>`
        : `<span style="background:var(--warn);color:#fff;border-radius:5px;padding:1px 7px;font-size:11px;font-weight:700;">수정</span>`;
      let detail;
      if (isCancel) {
        detail = "요청을 취소함";
      } else {
        const ch = diffChangedFields(h.beforeJson, h.afterJson);
        detail = ch.length ? ("바뀐 항목: " + ch.join(", ")) : "내용 저장";
      }
      return `<div style="padding:8px 0;border-bottom:1px solid var(--border);">
        <div style="display:flex;flex-wrap:wrap;gap:6px;align-items:center;font-size:13px;">
          ${badge}
          <b style="color:var(--text);">${esc(h.actor) || "(미입력)"}</b>
          <span style="color:var(--text-muted);">${esc(h.changedAt)}</span>
        </div>
        <div style="font-size:12.5px;color:var(--text-muted);margin-top:3px;">${esc(detail)}</div>
        <div style="font-size:12.5px;color:var(--text);margin-top:2px;">사유: ${esc(h.reason) || "-"}</div>
      </div>`;
    }).join("");
  }

  /** 상세보기의 변경 이력을 서버에서 불러옵니다. (행 펼칠 때 1회) */
  const HISTORY_HTML = {};   // 요청번호 → 변경이력 HTML (표가 다시 그려질 때 즉시 복원용)

  async function loadHistoryInto(sr) {
    const box = document.querySelector(`[data-history-list="${CSS.escape(sr)}"]`);
    if (!box || box.dataset.loaded === "1") return;
    box.dataset.loaded = "1";
    if (HISTORY_HTML[sr] !== undefined) { box.innerHTML = HISTORY_HTML[sr]; return; }
    try {
      const res = await fetch(API_CREATE + "/" + encodeURIComponent(sr) + "/history");
      if (!res.ok) throw new Error("서버 응답 " + res.status);
      const html = renderHistory(await res.json());
      HISTORY_HTML[sr] = html;
      box.innerHTML = html;
    } catch (e) {
      box.dataset.loaded = "";
      box.innerHTML = `<span style="font-size:13px;color:var(--danger);">이력을 불러오지 못했어요. 잠시 후 다시 열어 주세요.</span>`;
    }
  }

  /** 수정·취소 시 변경자 이름 + 사유를 입력받는 작은 창.
   *  {actor, reason} 또는 취소 시 null 반환. opts.bulkRemaining(2 이상)을 주면
   *  "이 내용으로 남은 N건 모두 일괄 적용" 버튼이 추가로 뜨고, 누르면 {actor, reason, bulkAll:true}를 반환합니다. */
  function askChange(actionLabel, opts) {
    const bulkRemaining = (opts && opts.bulkRemaining) || 0;
    return new Promise((resolve) => {
      let ov = document.getElementById("changeModal");
      if (!ov) {
        ov = document.createElement("div");
        ov.id = "changeModal";
        ov.style.cssText = "position:fixed;inset:0;background:rgba(0,0,0,.45);display:flex;align-items:center;justify-content:center;z-index:9999;padding:16px;";
        ov.innerHTML = `
          <div style="background:var(--surface);border:1px solid var(--border);border-radius:14px;max-width:400px;width:100%;padding:20px;box-shadow:0 12px 44px rgba(0,0,0,.22);">
            <div id="cmTitle" style="font-size:16px;font-weight:700;margin-bottom:4px;color:var(--text);"></div>
            <div style="font-size:13px;color:var(--text-muted);margin-bottom:14px;line-height:1.5;">여러 팀이 함께 쓰는 대장부라, 변경 기록을 남겨요. 두 칸 모두 입력해 주세요.</div>
            <label style="font-size:12px;font-weight:600;color:var(--text-muted);">변경자 이름 <span style="color:var(--danger);">*</span></label>
            <input id="cmActor" type="text" placeholder="예: 신혜인" style="width:100%;margin:4px 0 12px;padding:9px 10px;border:1px solid var(--border);border-radius:8px;font-size:14px;background:var(--surface-alt,#fff);color:var(--text);box-sizing:border-box;">
            <label style="font-size:12px;font-weight:600;color:var(--text-muted);">사유 <span style="color:var(--danger);">*</span></label>
            <input id="cmReason" type="text" placeholder="예: 수량 정정 / 수령지 변경" style="width:100%;margin:4px 0 4px;padding:9px 10px;border:1px solid var(--border);border-radius:8px;font-size:14px;background:var(--surface-alt,#fff);color:var(--text);box-sizing:border-box;">
            <div id="cmErr" style="font-size:12px;color:var(--danger);min-height:16px;margin-bottom:8px;"></div>
            <button id="cmBulk" type="button" class="btn ghost" style="display:none;width:100%;margin-bottom:8px;font-size:13px;color:var(--primary,#2563eb);"></button>
            <div style="display:flex;gap:8px;justify-content:flex-end;">
              <button id="cmCancel" type="button" class="btn ghost">닫기</button>
              <button id="cmOk" type="button" class="btn primary">확인</button>
            </div>
          </div>`;
        document.body.appendChild(ov);
      }
      const title = ov.querySelector("#cmTitle");
      const actor = ov.querySelector("#cmActor");
      const reason = ov.querySelector("#cmReason");
      const err = ov.querySelector("#cmErr");
      const bulkBtn = ov.querySelector("#cmBulk");
      title.textContent = actionLabel + " — 변경 기록 남기기";
      actor.value = ""; reason.value = ""; err.textContent = "";
      if (bulkRemaining > 1) {
        bulkBtn.textContent = `이 내용으로 남은 ${bulkRemaining}건 모두 일괄 적용`;
        bulkBtn.style.display = "block";
      } else {
        bulkBtn.style.display = "none";
      }
      ov.style.display = "flex";
      setTimeout(() => actor.focus(), 30);

      const cleanup = () => {
        okBtn.removeEventListener("click", onOk);
        cancelBtn.removeEventListener("click", onCancel);
        bulkBtn.removeEventListener("click", onBulk);
        ov.removeEventListener("keydown", onKey);
      };
      const close = (result) => { ov.style.display = "none"; cleanup(); resolve(result); };
      const readValid = () => {
        const a = actor.value.trim(), r = reason.value.trim();
        if (!a) { err.textContent = "변경자 이름을 입력해 주세요."; actor.focus(); return null; }
        if (!r) { err.textContent = "사유를 입력해 주세요."; reason.focus(); return null; }
        return { actor: a, reason: r };
      };
      const onOk = () => { const v = readValid(); if (v) close(v); };
      const onBulk = () => { const v = readValid(); if (v) close({ ...v, bulkAll: true }); };
      const onCancel = () => close(null);
      const onKey = (e) => {
        if (e.key === "Enter") { e.preventDefault(); onOk(); }
        else if (e.key === "Escape") { e.preventDefault(); onCancel(); }
      };
      const okBtn = ov.querySelector("#cmOk");
      const cancelBtn = ov.querySelector("#cmCancel");
      okBtn.addEventListener("click", onOk);
      cancelBtn.addEventListener("click", onCancel);
      bulkBtn.addEventListener("click", onBulk);
      ov.addEventListener("keydown", onKey);
    });
  }

  /** 상세보기의 첨부파일 영역을 서버에서 불러와 다운로드 링크로 채웁니다. (행을 펼칠 때 1회 호출) */
  // 요청별 첨부 목록 캐시 — 첨부 링크와 원본파일 미리보기가 같은 결과를 함께 씁니다. (요청 1번만)
  const ATTACH_PROMISE = {};
  function fetchAttachList(sr) {
    if (!ATTACH_PROMISE[sr]) {
      ATTACH_PROMISE[sr] = fetch(API_BASE + "/api/attachments/by-request/" + encodeURIComponent(sr))
        .then(res => { if (!res.ok) throw new Error("서버 응답 " + res.status); return res.json(); })
        .catch(e => { delete ATTACH_PROMISE[sr]; throw e; });   // 실패하면 다음에 다시 시도
    }
    return ATTACH_PROMISE[sr];
  }

  async function loadAttachmentsInto(sr) {
    const box = document.querySelector(`[data-attach-list="${CSS.escape(sr)}"]`);
    if (!box || box.dataset.loaded === "1") return;
    box.dataset.loaded = "1";
    try {
      const list = await fetchAttachList(sr);
      box.innerHTML = renderAttachLinks(list);
    } catch (e) {
      box.dataset.loaded = "";  // 실패하면 다음에 다시 시도할 수 있게
      box.innerHTML = `<span style="font-size:13px;color:var(--danger);">첨부를 불러오지 못했어요. 잠시 후 다시 열어 주세요.</span>`;
    }
  }

  /* ===================================================================
     ⑤ 요청자 원본 파일 미리보기 (다운로드 없이 눈으로 대조)
     요청자가 발주서/다중배송 양식을 올려 만든 요청서는, 우리가 자동 매칭한
     상품·수령정보가 원본과 다를 수 있어요. 상세보기 오른쪽에 원본 표를
     그대로 띄워서 바로 대조할 수 있게 합니다.
     =================================================================== */
  const PREVIEW_MAX_ROWS = 60;   // 화면에 그릴 최대 줄 수 (나머지는 "…" 안내)
  const PREVIEW_MAX_COLS = 14;   // 화면에 그릴 최대 칸 수
  const PREVIEWABLE = /\.(xlsx|xlsm|xls|csv|tsv|txt)$/i;

  const SRCPREV_HTML = {};   // 요청번호 → 미리보기 HTML (한 번 만들면 다시 쓰기)

  // '다시 시도' 버튼 — 실패했을 때만 나타납니다. 캐시를 비우고 다시 읽습니다.
  document.addEventListener("click", (e) => {
    const b = e.target.closest("[data-srcretry]");
    if (!b) return;
    e.stopPropagation();
    const sr = b.dataset.srcretry;
    delete SRCPREV_HTML[sr];
    delete ATTACH_PROMISE[sr];
    document.querySelectorAll(`[data-srcprev="${CSS.escape(sr)}"]`).forEach(c => {
      c.dataset.loaded = "";
      c.style.display = "";
      const body = c.querySelector(".srcprev-body");
      if (body) body.innerHTML = `<span style="font-size:12.5px;color:var(--text-muted);">다시 읽는 중…</span>`;
    });
    loadSourcePreviewInto(sr);
  });

  async function loadSourcePreviewInto(sr) {
    // 같은 요청이 표와 팝업에 동시에 열려 있을 수 있어, 화면에 있는 모든 미리보기 칸을 채웁니다.
    const cards = [...document.querySelectorAll(`[data-srcprev="${CSS.escape(sr)}"]`)].filter(c => c.dataset.loaded !== "1");
    if (!cards.length) return;
    cards.forEach(c => { c.dataset.loaded = "1"; });
    const grids = [...document.querySelectorAll(`[data-detailgrid="${CSS.escape(sr)}"]`)];
    const paint = (html) => cards.forEach(c => { const b = c.querySelector(".srcprev-body"); if (b) b.innerHTML = html; });
    const collapse = () => {
      grids.forEach(g => { g.style.gridTemplateColumns = "1fr"; });
      cards.forEach(c => { c.style.display = "none"; });
    };

    if (SRCPREV_HTML[sr] !== undefined) {
      if (SRCPREV_HTML[sr] === "") collapse(); else paint(SRCPREV_HTML[sr]);
      return;
    }

    let list = [];
    try {
      list = await fetchAttachList(sr);
    } catch (e) {
      cards.forEach(c => { c.dataset.loaded = ""; });
      paint(`<span style="font-size:12.5px;color:var(--danger);">원본 파일 목록을 불러오지 못했어요.</span>`);
      return;
    }
    if (!Array.isArray(list) || !list.length) { SRCPREV_HTML[sr] = ""; collapse(); return; }

    // 파일 이름을 먼저 보여줘서, 읽는 동안 멈춰 있는 것처럼 보이지 않게 합니다.
    const headOf = (a) => {
      const url = API_BASE + "/api/attachments/" + encodeURIComponent(a.id) + "/download";
      return `<div class="srcname"><span style="flex:1;min-width:0;">📄 ${esc(a.fileName || "첨부파일")}</span>
        <a href="${url}" target="_blank" rel="noopener">내려받기</a></div>`;
    };
    paint(list.map(a => `<div class="srcfile">${headOf(a)}<div class="srcmore">읽는 중…</div></div>`).join(""));

    const parts = [];
    for (const a of list) {
      const url = API_BASE + "/api/attachments/" + encodeURIComponent(a.id) + "/download";
      const name = a.fileName || "첨부파일";
      const head = headOf(a);
      if (!PREVIEWABLE.test(name)) {
        parts.push(`<div class="srcfile">${head}<div class="srcmore">이 형식은 화면 미리보기를 지원하지 않아요 — 내려받아서 확인해 주세요.</div></div>`);
        continue;
      }
      try {
        // 서버가 느릴 때 무한정 기다리지 않도록 20초에서 끊습니다.
        const ctl = new AbortController();
        const timer = setTimeout(() => ctl.abort(), 20000);
        let res;
        try { res = await fetch(url, { signal: ctl.signal }); } finally { clearTimeout(timer); }
        if (!res.ok) throw new Error("서버 응답 " + res.status);
        const buf = await res.arrayBuffer();
        const req = DATA.find(x => x.requestId === sr);
        parts.push(`<div class="srcfile">${head}${sheetsToHtml(buf, name, req)}</div>`);
      } catch (e) {
        const why = (e && e.name === "AbortError") ? "서버 응답이 20초를 넘었어요" : esc((e && e.message) || "오류");
        parts.push(`<div class="srcfile">${head}<div class="srcmore" style="color:var(--danger);">파일을 읽지 못했어요 (${why})
          <button type="button" class="btn ghost small" data-srcretry="${esc(sr)}" style="margin-left:6px;">다시 시도</button></div></div>`);
      }
    }
    const html = parts.join("")
      + `<div class="srcnote">요청서에 자동으로 매칭된 상품코드·옵션·수령정보가 이 원본과 같은지 대조해 주세요.
         다르면 상품표의 <b>🔧 상품 정정</b>(물류대장) 또는 <b>✏️ 선택 수정</b>으로 고칠 수 있어요.</div>`;
    SRCPREV_HTML[sr] = html;
    paint(html);
  }

  /* -------------------------------------------------------------------
     원본 ↔ 요청서 자동 대조
     자동 매칭이 틀렸는데 그대로 출고되는 사고를 막기 위한 장치입니다.
     ⚠️ 로 표시하는 건 "이름 규칙과 무관하게 확실히 문제인 것"만입니다.
       · 수량이 다름 / 줄 수가 다름 (누락·중복)
       · 수령자 이름·연락처·주소·배송메세지가 다름
     상품명·상품코드는 타팀 발주서와 이지어드민 정식 명칭이 원래 달라서
     (예: 원본은 바코드 88…, 요청서는 S코드) 틀렸다고 단정하지 않고
     **나란히 보여주기만** 합니다. 판단은 사람이 하도록.
     ------------------------------------------------------------------- */
  const _nText = (s) => String(s == null ? "" : s).replace(/\s+/g, "")
    .replace(/[\[\]()（）·.,\-_/#*★⭐"']/g, "").toLowerCase();
  const _nPhone = (s) => String(s == null ? "" : s).replace(/\D/g, "");
  const _nAddr = (s) => String(s == null ? "" : s).replace(/\s+/g, "")
    .replace(/[()（）,.\-]/g, "").toLowerCase();
  const _nQty = (s) => {
    const m = String(s == null ? "" : s).replace(/[^\d.-]/g, "");
    return m === "" ? null : Number(m);
  };

  /** 머리글에서 원하는 칸의 위치를 찾습니다. (정확히 같은 이름 → 포함하는 이름) */
  function pickCol(header, names) {
    const H = header.map(h => _nText(h));
    for (const n of names) { const k = _nText(n); const i = H.indexOf(k); if (i >= 0) return i; }
    for (const n of names) { const k = _nText(n); const i = H.findIndex(h => h && h.includes(k)); if (i >= 0) return i; }
    return -1;
  }

  const CMP_COLS = {
    order:  ["주문번호", "요청번호", "주문no", "orderno"],
    name:   ["상품명", "제품명", "품목명", "상품", "품명"],
    option: ["옵션", "옵션명", "옵션값", "옵션정보"],
    qty:    ["수량", "주문수량", "출고수량", "개수"],
    code:   ["상품코드", "바코드", "품목코드", "제품코드", "옵션코드", "sku"],
    rname:  ["수령자이름", "수령자", "수령인", "받는분", "받는사람", "수취인", "고객명"],
    phone:  ["수령자연락처", "연락처", "전화번호", "휴대폰", "수령자전화", "전화"],
    addr:   ["수령자주소", "주소", "배송지", "수령주소", "배송주소"],
    msg:    ["배송메세지", "배송메시지", "배송요청", "요청사항", "메세지", "메시지", "배송메모"],
  };

  /** 원본 표에서 "이 요청건에 해당하는 줄"만 골라냅니다.
   *  (다중 업로드로 한 파일에서 여러 요청서가 만들어졌을 수 있어서) */
  function pickRelatedRows(body, cols, r) {
    const all = body.map((_, i) => i).filter(i => body[i].some(c => String(c || "").trim()));
    if (cols.order >= 0) {
      const hit = all.filter(i => _nText(body[i][cols.order]) === _nText(r.requestId));
      if (hit.length) return { idx: hit, how: "주문번호" };
    }
    if (cols.rname >= 0 && r.receiver && r.receiver.name) {
      const hit = all.filter(i => _nText(body[i][cols.rname]) === _nText(r.receiver.name));
      if (hit.length && hit.length !== all.length) return { idx: hit, how: "수령자" };
      if (hit.length) return { idx: hit, how: null };
    }
    return { idx: all, how: null };
  }

  /** 대조 카드 HTML + 원본 표에서 흐리게 처리할 줄 번호를 함께 돌려줍니다. */
  function buildCompare(aoa, r) {
    if (!r) return { html: "", other: new Set() };
    const header = aoa[0] || [];
    const body = aoa.slice(1);
    const cols = {};
    Object.keys(CMP_COLS).forEach(k => { cols[k] = pickCol(header, CMP_COLS[k]); });
    // 우리가 아는 컬럼이 거의 없으면(형식이 전혀 다른 파일) 대조를 시도하지 않습니다.
    const known = ["name", "qty", "rname", "addr"].filter(k => cols[k] >= 0).length;
    if (known < 2) return { html: "", other: new Set() };

    const rel = pickRelatedRows(body, cols, r);
    const other = new Set(body.map((_, i) => i).filter(i => !rel.idx.includes(i)));
    const rows = rel.idx.map(i => body[i]);
    const warn = [];

    // ── 수령 정보 대조 ──
    const rc = r.receiver || {};
    const first = rows[0] || [];
    const infoRow = (label, colKey, mine, normFn) => {
      if (cols[colKey] < 0) return "";
      const src = String(first[cols[colKey]] ?? "").trim();
      const my = String(mine ?? "").trim();
      if (!src && !my) return "";
      const same = normFn(src) === normFn(my);
      // 값이 한쪽만 있으면 "확인 필요"로 봅니다. (원본에 있는데 요청서에 안 들어간 경우 등)
      if (!same) warn.push(label);
      return `<div class="cmp-row">
        <div class="k">${label}</div>
        <div class="v">${same
          ? esc(src || my)
          : `<span class="was">원본 ${esc(src) || "(없음)"}</span><span class="now">요청서 ${esc(my) || "(없음)"}</span>`}</div>
        <div class="s">${same ? "✅" : "⚠️"}</div>
      </div>`;
    };
    let infoHtml = "";
    infoHtml += infoRow("수령자", "rname", rc.name, _nText);
    infoHtml += infoRow("연락처", "phone", rc.phone, _nPhone);
    infoHtml += infoRow("주소", "addr", rc.address, _nAddr);
    infoHtml += infoRow("배송메세지", "msg", rc.message, _nText);

    // ── 상품 줄 수 대조 ──
    const prods = r.products || [];
    const countSame = rows.length === prods.length;
    if (!countSame) warn.push("상품 줄 수");
    infoHtml += `<div class="cmp-row">
      <div class="k">상품 줄</div>
      <div class="v">${countSame
        ? `원본 ${rows.length}줄 = 요청서 ${prods.length}종`
        : `<span class="was">원본 ${rows.length}줄</span><span class="now">요청서 ${prods.length}종</span>`}</div>
      <div class="s">${countSame ? "✅" : "⚠️"}</div>
    </div>`;

    // ── 상품 한 줄씩 대조 (순서대로 짝지음. 수량만 판정, 이름·코드는 나란히 표시) ──
    const n = Math.max(rows.length, prods.length);
    let itemHtml = "";
    for (let i = 0; i < n; i++) {
      const row = rows[i];
      const p = prods[i];
      const srcName = row && cols.name >= 0 ? String(row[cols.name] ?? "").trim() : "";
      const srcOpt = row && cols.option >= 0 ? String(row[cols.option] ?? "").trim() : "";
      const srcCode = row && cols.code >= 0 ? String(row[cols.code] ?? "").trim() : "";
      const srcQty = row && cols.qty >= 0 ? _nQty(row[cols.qty]) : null;
      const myQty = p ? _nQty(p.qty) : null;
      const qtyBad = (srcQty != null && myQty != null && srcQty !== myQty);
      const missing = !row || !p;
      if (qtyBad) warn.push(`${i + 1}번 상품 수량`);
      itemHtml += `<div class="cmp-item${(qtyBad || missing) ? " bad" : ""}">
        <div><span class="lbl">${i + 1} 원본</span>${row
          ? `${esc(srcName) || "(상품명 없음)"}${srcOpt ? ` · ${esc(srcOpt)}` : ""}${srcCode ? ` · <span class="cmp-code">${esc(srcCode)}</span>` : ""}${srcQty != null ? ` · <b${qtyBad ? ' class="qtybad"' : ""}>${srcQty}개</b>` : ""}`
          : `<span style="color:#C0392B;">원본에 없는 줄 — 요청서에만 있어요</span>`}</div>
        <div><span class="lbl">요청서</span>${p
          ? `${esc(p.name) || "(상품명 없음)"}${p.option ? ` · ${esc(p.option)}` : ""}${p.code ? ` · <span class="cmp-code">${esc(p.code)}</span>` : ""}${myQty != null ? ` · <b${qtyBad ? ' class="qtybad"' : ""}>${myQty}개</b>` : ""}`
          : `<span style="color:#C0392B;">요청서에 안 들어간 줄 — 누락 확인 필요</span>`}</div>
      </div>`;
    }

    const uniq = [...new Set(warn)];
    const badge = uniq.length
      ? `<span class="cmp-badge warn">⚠️ 확인 필요 ${uniq.length}곳</span>`
      : `<span class="cmp-badge ok">✅ 수량·수령정보 일치</span>`;
    const relNote = other.size
      ? `<div class="srcnote">이 파일에는 다른 요청건 줄도 있어요 — ${rel.how ? rel.how + "로 " : ""}이 요청건 ${rows.length}줄만 대조했고, 아래 표에서 나머지는 흐리게 표시했어요.</div>`
      : "";

    return {
      html: `<div class="cmp">
        <div class="cmp-head"><span class="t">🔍 원본 ↔ 요청서 대조</span>${badge}</div>
        ${infoHtml}
        <div class="cmp-sub">상품 대조 <span style="font-weight:400;">(상품명·코드는 원래 다를 수 있어요 — 눈으로 확인용)</span></div>
        ${itemHtml}
        ${uniq.length ? `<div class="srcnote" style="color:#C0392B;">다른 곳: ${esc(uniq.join(", "))}</div>` : ""}
        ${relNote}
      </div>`,
      other,
    };
  }

  /** 엑셀/CSV 바이트 → (대조 카드 +) 시트별 표 HTML (첫 줄을 머리글로 봅니다) */
  function sheetsToHtml(buf, fileName, req) {
    let wb;
    try {
      wb = XLSX.read(new Uint8Array(buf), { type: "array" });
    } catch (e) {
      return `<div class="srcmore" style="color:var(--danger);">엑셀 형식을 읽지 못했어요.</div>`;
    }
    const out = [];
    let cmpDone = false;
    wb.SheetNames.slice(0, 3).forEach(sn => {
      const aoa = XLSX.utils.sheet_to_json(wb.Sheets[sn], { header: 1, defval: "", blankrows: false });
      if (!aoa.length) return;
      // 첫 번째(내용이 있는) 시트에서만 자동 대조를 합니다.
      let otherRows = new Set();
      if (!cmpDone) {
        const cmp = buildCompare(aoa, req);
        if (cmp.html) { out.push(cmp.html); otherRows = cmp.other; }
        cmpDone = true;
      }
      const colCount = Math.min(PREVIEW_MAX_COLS, aoa.reduce((m, row) => Math.max(m, row.length), 0));
      const cell = (val) => {
        const s = (val == null) ? "" : String(val);
        return `<td title="${esc(s)}">${esc(s)}</td>`;
      };
      const header = aoa[0] || [];
      const bodyRows = aoa.slice(1, 1 + PREVIEW_MAX_ROWS);
      const thead = `<thead><tr><th class="rowno">#</th>${
        Array.from({ length: colCount }, (_, i) => `<th title="${esc(String(header[i] ?? ""))}">${esc(String(header[i] ?? "")) || "&nbsp;"}</th>`).join("")
      }</tr></thead>`;
      // 이 요청건과 관계없는 줄(다중 업로드로 만들어진 다른 요청서)은 흐리게 표시
      const tbody = `<tbody>${bodyRows.map((row, ri) => `<tr${otherRows.has(ri) ? ' class="other" title="다른 요청건 줄이에요"' : ""}><td class="rowno">${ri + 1}</td>${
        Array.from({ length: colCount }, (_, i) => cell(row[i])).join("")
      }</tr>`).join("")}</tbody>`;
      const more = (aoa.length - 1 > bodyRows.length)
        ? `<div class="srcmore">… 아래 ${(aoa.length - 1 - bodyRows.length).toLocaleString()}줄은 생략했어요 (전체 ${(aoa.length - 1).toLocaleString()}줄)</div>` : "";
      const sheetLabel = (wb.SheetNames.length > 1) ? `<div class="srcmore" style="padding:4px 0;">시트: <b>${esc(sn)}</b></div>` : "";
      out.push(`${sheetLabel}<div class="srcwrap"><table>${thead}${tbody}</table></div>${more}`);
    });
    if (!out.length) return `<div class="srcmore">내용이 비어 있어요.</div>`;
    return out.join("");
  }

  /** 첨부 메타 목록 → 다운로드 링크 HTML */
  function renderAttachLinks(list) {
    if (!Array.isArray(list) || !list.length) {
      return `<span style="font-size:13px;color:var(--text-muted);">첨부된 파일이 없어요.</span>`;
    }
    return list.map(a => {
      const kb = a.byteSize ? Math.max(1, Math.round(a.byteSize / 1024)) : 0;
      const size = kb ? ` <span style="color:var(--text-muted);">(${kb.toLocaleString()} KB)</span>` : "";
      const url = API_BASE + "/api/attachments/" + encodeURIComponent(a.id) + "/download";
      return `<div style="padding:4px 0;">📄 <a href="${url}" target="_blank" rel="noopener" style="color:var(--accent);text-decoration:underline;">${esc(a.fileName) || "첨부파일"}</a>${size}</div>`;
    }).join("");
  }

  /** 송장 정보 영역 — 세 가지 상태: 미연결 / 연결됨(대기중) / 등록완료 */
  function trackingBoxHtml(r) {
    if (r.trackingNo) {
      // 이미 등록 완료 (자동이든 수기든)
      return `<div class="drow"><div class="k">택배사</div><div class="v">${esc(r.carrier || "-")}</div></div>
        <div class="drow"><div class="k">송장번호</div><div class="v"><span class="num">${esc(r.trackingNo)}</span></div></div>
        <div class="drow"><div class="k">상태</div><div class="v"><span style="color:var(--good);font-weight:700;">등록 완료</span></div></div>`;
    }
    if (r.ezadminSeq) {
      // 연결은 됐고, 자동 확인 대기 중
      return `<div class="drow"><div class="k">이지어드민 관리번호</div>
        <div class="v"><span class="num">${esc(r.ezadminSeq)}</span>
          <span style="color:var(--text-muted);">— 송장이 등록되면 자동으로 채워져요 (최대 5분 주기로 확인해요)</span>
          <div style="margin-top:9px;"><button type="button" class="btn ghost small linkOrderBtn" data-id="${esc(r.requestId)}">번호 다시 연결</button></div>
        </div>
      </div>`;
    }
    // 아직 연결 안 함
    return `<div class="drow"><div class="k">이지어드민 관리번호</div>
      <div class="v" style="display:flex;gap:8px;align-items:center;flex-wrap:wrap;">
        <input type="text" class="linkSeqInput" data-id="${esc(r.requestId)}" placeholder="예: 128841">
        <button type="button" class="btn small linkOrderBtn" data-id="${esc(r.requestId)}">연결</button>
      </div>
    </div>`;
  }

  function slackText(r) {
    const lines = [];
    lines.push(`[출고 요청서] ${r.requestId}`);
    lines.push(`요청팀: ${r.team || "-"} / 요청자: ${r.requester || "-"}${r.assignee ? ` / 담당자: ${r.assignee}` : ""}`);
    lines.push(`판매처: ${channelText(r) || "미지정"}`);
    lines.push(`요청일자: ${r.requestDate || "-"}`);
    lines.push(`출고 희망일: ${r.desiredDate || "-"}`);
    lines.push(`구분: ${r.billing ? BILLING_LABEL[r.billing] : "미지정"}`);
    if (r.content) lines.push(`요청내용: ${r.content}`);
    lines.push("");
    lines.push("■ 상품 정보");
    if (r.products.length) {
      r.products.forEach((p, i) => {
        const bits = [];
        if (p.code) bits.push(`[${p.code}]`);
        bits.push(p.name || "(상품명 미기재)");
        if (p.option) bits.push(`/ 옵션: ${p.option}`);
        bits.push(`/ 수량: ${p.qty ?? "-"}`);
        lines.push(` ${i + 1}. ${bits.join(" ")}`);
      });
      const sum = totalQty(r);
      if (sum) lines.push(` (총 ${r.products.length}종 / ${sum}개)`);
    } else {
      lines.push(" (상품 정보 확인 필요)");
    }
    lines.push("");
    lines.push("■ 수령 정보");
    lines.push(` 수령자: ${r.receiver.name || "-"}${r.receiver.phone ? ` (${r.receiver.phone})` : ""}`);
    lines.push(` 주소: ${r.receiver.address || "-"}`);
    if (r.receiver.message) lines.push(` 배송 메세지: ${r.receiver.message}`);
    lines.push("");
    lines.push(`상태: ${STATUS_LABEL[r.status]}`);
    return lines.join("\n");
  }

  function copyText(text, btn) {
    const done = () => {
      if (!btn) return;
      const old = btn.textContent;
      btn.textContent = "복사됨";
      setTimeout(() => { btn.textContent = old; }, 1600);
    };
    const fallback = () => {
      const ta = document.createElement("textarea");
      ta.value = text;
      ta.style.position = "fixed"; ta.style.opacity = "0";
      document.body.appendChild(ta); ta.select();
      try { document.execCommand("copy"); done(); }
      catch (e) { if (btn) { btn.textContent = "복사 실패"; setTimeout(() => { btn.textContent = "슬랙용 텍스트 복사"; }, 1600); } }
      document.body.removeChild(ta);
    };
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(text).then(done).catch(fallback);
    } else { fallback(); }
  }

  function getFilteredRows() {
    let rows = DATA.slice();
    // 검색어·요청자·기간으로 찾는 중이면, "진행 필요/대기/진행중" 탭에 걸려서
    // 완료건이 안 보이는 일이 없도록 상태 탭 필터는 건너뜁니다 — 찾는 게 최우선.
    // (취소 내역/안전재고/완료 내역처럼 화면 자체가 다른 경우는 그대로 유지합니다)
    const searching = !!(searchTerm || activeRequester || dateRangeFrom || dateRangeTo);
    if (activeView === "cancelled") {
      // 취소 내역 화면 — 취소된 건만 모아서 보여줍니다. (상태 탭 필터는 적용하지 않음)
      rows = rows.filter(r => r.status === "cancelled");
    } else if (activeView === "safety") {
      // 안전재고 화면 — 안전재고(재고확보) 요청만 모아서 보여줍니다.
      // 완료건까지 대기·진행중이랑 계속 섞여서 지저분하다는 피드백으로, 다른 화면과 똑같이
      // 상태 탭(진행 필요/대기/진행중/완료/전체)으로 나눠서 볼 수 있게 했습니다.
      rows = rows.filter(r => r.requestType === "안전재고" && r.status !== "cancelled");
      if (!searching) {
        if (activeStatus === "open") rows = rows.filter(r => r.status === "pending" || r.status === "in_progress");
        else if (activeStatus) rows = rows.filter(r => r.status === activeStatus);
      }
    } else {
      // 그 외 모든 화면(대시보드·전체·완료)에서는 취소 건과 안전재고 건을 숨깁니다.
      rows = rows.filter(r => r.status !== "cancelled" && r.requestType !== "안전재고");
      if (activeView === "done") {
        rows = rows.filter(r => r.status === "done");
      } else if (!searching) {
        // "진행 필요" 탭 = 대기 + 진행중 (완료·취소 제외) — 실제로 손이 가야 하는 건만
        if (activeStatus === "open") rows = rows.filter(r => r.status === "pending" || r.status === "in_progress");
        else if (activeStatus) rows = rows.filter(r => r.status === activeStatus);
      }
    }
    if (activeTeam) rows = rows.filter(r => r.team === activeTeam);
    if (activeAssignee) rows = rows.filter(r => r.assignee === activeAssignee);
    if (activeChannel) rows = rows.filter(r => r.channels.includes(activeChannel));
    if (activeRequester) rows = rows.filter(r => r.requester === activeRequester);
    if (dateRangeFrom || dateRangeTo) {
      rows = rows.filter(r => {
        const v = String(r[dateRangeField] || "").slice(0, 10);
        if (!v) return false;
        if (dateRangeFrom && v < dateRangeFrom) return false;
        if (dateRangeTo && v > dateRangeTo) return false;
        return true;
      });
    }
    if (todayOnly) rows = rows.filter(r => r.status !== "done" && r.desiredDate === TODAY);
    if (searchTerm) {
      const t = searchTerm.toLowerCase();
      const hay = (r) => [
        r.requestId, r.team, r.requester, r.assignee, r.note, r.content, channelText(r),
        r.receiver.name, r.receiver.address, r.receiver.phone,
        ...r.products.map(p => `${p.code} ${p.name} ${p.option}`),
      ].join(" ").toLowerCase();
      rows = rows.filter(r => hay(r).includes(t));
    }
    rows.sort((a, b) => {
      // 1) 완료건은 항상 아래로, 아직 처리 안 끝난 건(대기·진행중)을 위로
      const aDone = a.status === "done", bDone = b.status === "done";
      if (aDone !== bDone) return aDone ? 1 : -1;
      // 2) 완료건끼리는 최근에 완료한 것부터
      if (aDone && bDone) return (b.completedAt || "").localeCompare(a.completedAt || "");
      // 3) 미완료건끼리는 지연 → 정보부족 → 출고희망일 순
      if (isOverdue(a) !== isOverdue(b)) return isOverdue(a) ? -1 : 1;
      if (hasMissingInfo(a) !== hasMissingInfo(b)) return hasMissingInfo(a) ? -1 : 1;
      return (a.desiredDate || "9999").localeCompare(b.desiredDate || "9999");
    });
    return rows;
  }

  // ===== 캘린더 보기 =====
  function ymNow(){ const p = (typeof TODAY === "string" && TODAY ? TODAY : "").split("-"); return { y: (+p[0]) || new Date().getFullYear(), m: ((+p[1]) || 1) - 1 }; }
  function pad2(n){ return String(n).padStart(2, "0"); }

  function renderCalendar(rows){
    const box = document.getElementById("calendarView");
    if(!calCur) calCur = ymNow();
    const y = calCur.y, m = calCur.m;
    const byDate = {};
    rows.forEach(r => { const d = r.desiredDate; if(d){ (byDate[d] = byDate[d] || []).push(r); } });
    calByDate = byDate;   // '+N건 더' 팝업이 캘린더와 똑같은(필터 반영된) 목록을 쓰도록 저장
    const first = new Date(y, m, 1).getDay();
    const days = new Date(y, m + 1, 0).getDate();
    const prevDays = new Date(y, m, 0).getDate();
    const cells = [];
    for(let i = 0; i < first; i++) cells.push({ out:true, d: prevDays - first + 1 + i });
    for(let d = 1; d <= days; d++) cells.push({ out:false, d });
    while(cells.length % 7 !== 0) cells.push({ out:true, d: cells.length });
    let gridHtml = "";
    cells.forEach(c => {
      const key = y + "-" + pad2(m + 1) + "-" + pad2(c.d);
      const isToday = (!c.out && key === TODAY);
      let chips = "";
      if(!c.out && byDate[key]){
        const list = byDate[key];
        // 팀별로 묶어서 "팀명 N건" 칩 하나씩 (같은 팀이 여러 건이어도 한 줄로 정리)
        const teams = {};
        list.forEach(r => { const t = (r.team || r.requester || "기타"); (teams[t] = teams[t] || []).push(r); });
        const teamNames = Object.keys(teams);
        teamNames.slice(0, 4).forEach(t => {
          const items = teams[t];
          const cls = calDominantStatus(items);   // 대기>진행중>완료 순으로 대표 색
          chips += `<div class="cchip ${cls}" data-openday="${key}" data-team="${encodeURIComponent(t)}" title="${esc(t)} ${items.length}건 — 누르면 목록이 열려요">${esc(t)} <b>${items.length}</b>건</div>`;
        });
        if(teamNames.length > 4) chips += `<div class="cmore" data-openday="${key}">+${teamNames.length - 4}팀 더</div>`;
      }
      gridHtml += `<div class="ccell${c.out ? " out" : ""}${isToday ? " ctodaycell" : ""}"><div class="cdaynum">${c.d}</div>${chips}</div>`;
    });
    box.innerHTML =
      `<div class="calbar">
        <div class="cnav"><button data-cal="prev">‹</button><span class="cmon">${y}년 ${m + 1}월</span><button data-cal="next">›</button></div>
        <button class="ctoday" data-cal="today">오늘</button>
        <div class="callegend">
          <span><span class="cdot" style="background:var(--accent)"></span>대기</span>
          <span><span class="cdot" style="background:var(--warn)"></span>진행중</span>
          <span><span class="cdot" style="background:var(--good,#12925a)"></span>완료</span>
          <span style="opacity:.7;">· 출고 희망일 기준</span>
        </div>
      </div>
      <div class="caldow"><div>일</div><div>월</div><div>화</div><div>수</div><div>목</div><div>금</div><div>토</div></div>
      <div class="calgrid">${gridHtml}</div>`;
    box.querySelectorAll("[data-cal]").forEach(b => b.addEventListener("click", () => {
      const act = b.dataset.cal;
      if(act === "today") calCur = ymNow();
      else { let nm = calCur.m + (act === "next" ? 1 : -1), ny = calCur.y; if(nm < 0){ nm = 11; ny--; } if(nm > 11){ nm = 0; ny++; } calCur = { y:ny, m:nm }; }
      renderTable();
    }));
    box.querySelectorAll("[data-openid]").forEach(el => el.addEventListener("click", (e) => { e.stopPropagation(); openRequestDetail(el.dataset.openid); }));
    // 팀 칩을 누르면 그 팀의 그날 요청 목록, '+N팀 더'를 누르면 그날 전체(팀별) 목록을 보여줍니다.
    box.querySelectorAll("[data-openday]").forEach(el => el.addEventListener("click", (e) => {
      e.stopPropagation();
      const t = el.dataset.team ? decodeURIComponent(el.dataset.team) : null;
      openDayList(el.dataset.openday, t);
    }));
  }

  // 그날 요청들의 대표 상태색: 대기(있으면) > 진행중 > 완료 순
  function calDominantStatus(items){
    if(items.some(r => r.status === "pending")) return "pending";
    if(items.some(r => r.status === "in_progress")) return "in_progress";
    return "done";
  }

  // 캘린더에서 특정 날짜(출고 희망일)의 요청 전체를 목록으로 보여주는 팝업.
  // 각 줄을 누르면 그 요청 상세가 열립니다.
  function openDayList(dateKey, team){
    if(!dateKey) return;
    let list = (calByDate && calByDate[dateKey]) ? calByDate[dateKey] : DATA.filter(r => r.desiredDate === dateKey);
    if(team) list = list.filter(r => (r.team || r.requester || "기타") === team);
    if(!list.length) return;
    let ov = document.getElementById("dayModal");
    if(!ov){
      ov = document.createElement("div");
      ov.id = "dayModal";
      ov.className = "reqmodal-overlay";
      ov.innerHTML = '<div class="reqmodal-panel" id="dayModalPanel"></div>';
      document.body.appendChild(ov);
      ov.addEventListener("click", e => { if(e.target.id === "dayModal") ov.style.display = "none"; });
      document.addEventListener("keydown", e => { if(e.key === "Escape") ov.style.display = "none"; });
    }
    const panel = document.getElementById("dayModalPanel");

    // 팀별로 묶어서 보여줍니다(특정 팀만 열었으면 그 팀 그룹만).
    const groups = {};
    list.forEach(r => { const t = (r.team || r.requester || "기타"); (groups[t] = groups[t] || []).push(r); });
    const rowHtml = (r) => `
      <div class="dayrow" data-openid="${esc(r.requestId)}" tabindex="0">
        <span class="statuspill ${r.status}">${STATUS_LABEL[r.status] || r.status}</span>
        <span class="dayrow-id">${esc(r.requestId)}</span>
        <span class="dayrow-sum" title="${esc(productSummary(r))}">${esc(productSummary(r)) || "-"}</span>
        <span class="dayrow-qty num">${totalQty(r) ?? "-"}개</span>
      </div>`;
    const groupsHtml = Object.keys(groups).map(t => `
      <div class="dayteam">
        <div class="dayteam-head">${esc(t)} <span class="dayteam-cnt">${groups[t].length}건</span></div>
        ${groups[t].map(rowHtml).join("")}
      </div>`).join("");

    const titleTeam = team ? ` · ${esc(team)}` : "";
    panel.innerHTML =
      `<div class="reqmodal-head">
        <strong style="margin-right:auto;font-size:15px;color:var(--text);">📅 ${esc(dateKey)}${titleTeam} · 출고 희망 ${list.length}건</strong>
        <button type="button" class="btn ghost small" id="dmClose">닫기 ✕</button>
      </div>
      <div class="daylist">${groupsHtml}</div>`;
    ov.style.display = "flex";
    panel.querySelector("#dmClose").onclick = () => { ov.style.display = "none"; };
    panel.querySelectorAll("[data-openid]").forEach(el => {
      const open = () => { ov.style.display = "none"; openRequestModal(el.dataset.openid); };
      el.addEventListener("click", open);
      el.addEventListener("keydown", e => { if(e.key === "Enter" || e.key === " "){ e.preventDefault(); open(); } });
    });
  }

  // ===== 칸반 보기 =====
  function renderKanban(rows){
    const box = document.getElementById("kanbanView");
    const cols = [
      { key:"pending", label:"대기", cls:"" },
      { key:"in_progress", label:"진행중", cls:"in_progress" },
      { key:"done", label:"완료", cls:"done" },
    ];
    let html = `<div class="kanban">`;
    cols.forEach(col => {
      const list = rows.filter(r => r.status === col.key);
      html += `<div class="kcol"><h4>${col.label} <span class="kcount">${list.length}</span></h4>`;
      if(!list.length) html += `<div class="kempty">없음</div>`;
      list.forEach(r => {
        html += `<div class="kcard ${col.cls}" data-openid="${esc(r.requestId)}">
          <div class="ksr">${esc(r.requestId)}</div>
          <div class="kitem">${esc(productSummary(r)) || "-"}</div>
          <div class="kmeta"><span>${esc(r.team) || "-"} · ${esc(r.requester) || "-"}</span><span>🚚 ${esc(r.desiredDate) || "-"}</span></div>
        </div>`;
      });
      html += `</div>`;
    });
    html += `</div>`;
    box.innerHTML = html;
    box.querySelectorAll("[data-openid]").forEach(el => el.addEventListener("click", () => openRequestDetail(el.dataset.openid)));
  }

  // 캘린더/칸반에서 항목을 누르면 → 그 요청 "한 건만" 팝업으로 엽니다. (전체 목록으로 안 바뀜)
  function openRequestDetail(sr){ openRequestModal(sr); }

  function closeReqModal(){ const ov = document.getElementById("reqModal"); if(ov) ov.style.display = "none"; }

  function openRequestModal(sr){
    if(!sr) return;
    const r = DATA.find(x => x.requestId === sr);
    if(!r) return;
    let ov = document.getElementById("reqModal");
    if(!ov){
      ov = document.createElement("div");
      ov.id = "reqModal";
      ov.className = "reqmodal-overlay";
      ov.innerHTML = '<div class="reqmodal-panel" id="reqModalPanel"></div>';
      document.body.appendChild(ov);
      ov.addEventListener("click", e => { if(e.target.id === "reqModal") closeReqModal(); });
      document.addEventListener("keydown", e => { if(e.key === "Escape") closeReqModal(); });
    }
    const panel = document.getElementById("reqModalPanel");
    const canEdit = r.status !== "cancelled";
    panel.innerHTML =
      `<div class="reqmodal-head">
        <button type="button" class="btn ghost small" id="rmClose">닫기 ✕</button>
      </div>${detailHtml(r)}`;
    ov.style.display = "flex";
    loadAttachmentsInto(sr);
    loadHistoryInto(sr);
    loadSourcePreviewInto(sr);
    refreshLineStocks();
    panel.querySelector("#rmClose").onclick = closeReqModal;
    panel.querySelectorAll("[data-copy]").forEach(b => b.onclick = () => { const rr = DATA.find(x => x.requestId === b.dataset.copy); if(rr) copyText(slackText(rr), b); });
    panel.querySelectorAll("[data-memo]").forEach(b => b.onclick = () => openMemo(b.dataset.memo));
    panel.querySelectorAll(".linkOrderBtn").forEach(b => b.onclick = () => {
      const input = panel.querySelector(`.linkSeqInput[data-id="${CSS.escape(b.dataset.id)}"]`);
      const seq = input ? input.value : prompt("이지어드민 관리번호를 입력해 주세요");
      if(seq != null) linkOrder(b.dataset.id, seq);
    });
  }

  function updateViewModeButtons(){
    document.querySelectorAll("#viewSwitch .vbtn").forEach(b => b.classList.toggle("on", b.dataset.vm === viewMode));
  }

  /** 상태 칸 — 안전재고 요청은 눌러서 대기 → 진행중 → 완료로 바로 넘길 수 있는 버튼으로 그립니다.
   *  (예전 '관리' 열을 없앨 때 버튼이 함께 사라져서 클릭이 안 되던 문제를 고쳤어요) */
  function statusCellHtml(r) {
    const label = STATUS_LABEL[r.status];
    const clickable = r.requestType === "안전재고" && r.status !== "cancelled";
    if (!clickable) return `<span class="statuspill ${r.status}">${label}</span>`;
    const nextLabel = SAFETY_NEXT_LABEL[r.status] || "진행중으로";
    return `<button type="button" class="statuspill ${r.status} safetyStatusBtn"
      data-id="${esc(r.requestId)}" data-status="${esc(r.status)}"
      title="눌러서 ${nextLabel} 바꿔요">${label}<span class="arrow">⇢</span></button>`;
  }

  /** ④ 페이지 이동 줄 — 30건 단위로 나눠 보여주고, 지금 몇 번째를 보고 있는지 알려줍니다. */
  function renderPager(total, totalPages, startIdx, shown) {
    const el = document.getElementById("pager");
    if (!el) return;
    if (!total) { el.style.display = "none"; el.innerHTML = ""; return; }
    el.style.display = "";

    // 페이지 번호 버튼: 현재 페이지 주변만 보여주고 나머지는 …로 접습니다.
    const nums = [];
    const push = (n) => nums.push(`<button type="button" class="pnum${n === PAGE ? " on" : ""}" data-page="${n}">${n}</button>`);
    const gap = () => nums.push('<span class="gap">…</span>');
    if (totalPages <= 9) {
      for (let i = 1; i <= totalPages; i++) push(i);
    } else {
      push(1);
      if (PAGE > 4) gap();
      for (let i = Math.max(2, PAGE - 2); i <= Math.min(totalPages - 1, PAGE + 2); i++) push(i);
      if (PAGE < totalPages - 3) gap();
      push(totalPages);
    }
    el.innerHTML = `
      <span class="pinfo">전체 ${total.toLocaleString()}건 중 ${(startIdx + 1).toLocaleString()}–${(startIdx + shown).toLocaleString()}번째 · ${PAGE}/${totalPages} 페이지</span>
      <button type="button" data-page="prev" ${PAGE <= 1 ? "disabled" : ""} title="이전 페이지">‹ 이전</button>
      ${nums.join("")}
      <button type="button" data-page="next" ${PAGE >= totalPages ? "disabled" : ""} title="다음 페이지">다음 ›</button>
      <select id="pageSizeSel" title="한 페이지에 보여줄 건수">
        ${[30, 50, 100].map(n => `<option value="${n}"${n === PAGE_SIZE ? " selected" : ""}>${n}건씩</option>`).join("")}
      </select>`;

    el.querySelectorAll("button[data-page]").forEach(b => b.addEventListener("click", () => {
      const v = b.dataset.page;
      if (v === "prev") PAGE = Math.max(1, PAGE - 1);
      else if (v === "next") PAGE = Math.min(totalPages, PAGE + 1);
      else PAGE = Number(v) || 1;
      renderTable();
      const card = document.querySelector(".tablecard");
      if (card) card.scrollIntoView({ behavior: "smooth", block: "start" });
    }));
    const sizeSel = document.getElementById("pageSizeSel");
    if (sizeSel) sizeSel.addEventListener("change", () => {
      PAGE_SIZE = Number(sizeSel.value) || 30;
      PAGE = 1;
      renderTable();
    });
  }

  function renderTable() {
    const allRows = getFilteredRows();
    renderStatusTabCounts();

    // 보기 방식(목록/캘린더/칸반) 전환 — 같은 필터 결과(rows)를 다른 모양으로 그립니다.
    const listView = document.getElementById("listView");
    const calView = document.getElementById("calendarView");
    const kanView = document.getElementById("kanbanView");
    const pagerEl = document.getElementById("pager");
    if (listView) listView.style.display = viewMode === "list" ? "" : "none";
    if (calView) calView.style.display = viewMode === "calendar" ? "" : "none";
    if (kanView) kanView.style.display = viewMode === "kanban" ? "" : "none";
    if (pagerEl) pagerEl.style.display = "none";
    if (viewMode === "calendar") { renderCalendar(allRows); return; }
    if (viewMode === "kanban") { renderKanban(allRows); return; }

    // ④ 페이지 나누기 — 한 페이지 30건(기본). 필터가 바뀌면 자동으로 1페이지로 돌아갑니다.
    const sig = [activeView, activeStatus, activeTeam, activeAssignee, activeChannel, activeRequester, dateRangeField, dateRangeFrom, dateRangeTo, todayOnly, searchTerm, PAGE_SIZE].join("§");
    if (sig !== _pageSig) { _pageSig = sig; PAGE = 1; }
    const totalPages = Math.max(1, Math.ceil(allRows.length / PAGE_SIZE));
    if (PAGE > totalPages) PAGE = totalPages;
    if (PAGE < 1) PAGE = 1;
    const startIdx = (PAGE - 1) * PAGE_SIZE;
    const rows = allRows.slice(startIdx, startIdx + PAGE_SIZE);
    renderPager(allRows.length, totalPages, startIdx, rows.length);

    const body = document.getElementById("tableBody");
    document.getElementById("tableEmpty").style.display = allRows.length ? "none" : "";
    body.innerHTML = rows.map(r => {
      const open = expanded.has(r.requestId);
      const summary = productSummary(r);
      const qty = totalQty(r);
      const showMissing = hasMissingInfo(r) && r.status !== "done";
      return `<tr class="mainrow${open ? " open" : ""}" data-id="${esc(r.requestId)}" tabindex="0" aria-expanded="${open}">
        <td><input type="checkbox" class="rowSelect" data-id="${esc(r.requestId)}" title="이 주문건 선택" ${selected.has(r.requestId) ? "checked" : ""} style="width:15px;height:15px;accent-color:var(--accent);cursor:pointer;vertical-align:middle;"></td>
        <td class="num reqidcell">
          <span class="expandmark"><svg viewBox="0 0 24 24"><path d="m9 6 6 6-6 6"/></svg></span>${esc(r.requestId) || "-"}${r.source === "local" ? '<span class="local-tag" title="이 브라우저에만 저장된 항목이에요">로컬</span>' : ""}
        </td>
        <td class="num col-extra">${esc(r.requestDate) || "-"}</td>
        <td>${esc(r.team) || "-"}</td>
        <td>${esc(r.requester) || "-"}</td>
        <td>${r.billing ? `<span class="billpill ${r.billing}">${BILLING_LABEL[r.billing]}</span>` : '<span style="color:var(--text-muted);">-</span>'}</td>
        <td class="msg col-extra" title="${esc(channelText(r))}">${esc(channelText(r)) || "-"}</td>
        <td class="msg" title="${esc(summary)}">${esc(summary) || "-"}${showMissing ? '<span class="missing-tag">정보 확인 필요</span>' : ""}</td>
        <td class="num">${qty ?? "-"}</td>
        <td class="num col-extra" data-stock-code="${esc((r.products[0] && r.products[0].code) || "")}">${stockCellText(r)}</td>
        <td class="duecell col-due${isOverdue(r) ? " overdue" : isSoon(r) ? " soon" : ""}">${(isOverdue(r) || isSoon(r)) ? "🔥 " : ""}${fmtDue(r)}</td>
        <td class="col-recv">${esc(r.receiver.name) || "-"}</td>
        <td class="msg col-to col-extra" title="${esc(r.receiver.address)}">${esc(r.receiver.address) || "-"}</td>
        <td>${statusCellHtml(r)}</td>
        <td class="num col-track">${r.trackingNo ? `<span class="trackbadge" title="${esc(r.carrier || "")}">🚚 ${esc(r.trackingNo)}</span>` : "-"}</td>
        <td class="num col-extra">${esc(r.completedAt) || "-"}</td>
        <td class="col-memo memocell${r.memo ? " has-memo" : ""}" data-memo="${esc(r.requestId)}" title="${r.memo ? esc(r.memo) : "클릭해서 특이사항(비고) 메모를 작성해요"}">${r.memo ? `<span class="memo-text">📝 ${esc(r.memo.length > 22 ? r.memo.slice(0, 22) + "…" : r.memo)}</span>` : '<span class="memo-empty">＋ 메모</span>'}</td>
      </tr>
      <tr class="detailrow" data-detail="${esc(r.requestId)}"${open ? "" : ' style="display:none;"'}>
        <td colspan="17">${detailHtml(r)}</td>
      </tr>`;
    }).join("");

    body.querySelectorAll("tr.mainrow").forEach(tr => {
      const toggle = () => {
        const id = tr.dataset.id;
        const drow = body.querySelector(`tr[data-detail="${CSS.escape(id)}"]`);
        const open = expanded.has(id);
        if (open) { expanded.delete(id); } else { expanded.add(id); }
        tr.classList.toggle("open", !open);
        tr.setAttribute("aria-expanded", String(!open));
        if (drow) drow.style.display = open ? "none" : "";
        if (!open) { loadAttachmentsInto(id); loadHistoryInto(id); loadSourcePreviewInto(id); refreshLineStocks(); }   // 펼칠 때 첨부파일·변경이력·원본파일 미리보기 불러오고, 상품별 가용재고(상품코드·바코드) 즉시 조회
      };
      tr.addEventListener("click", (e) => { if (e.target.closest("button, input, a")) return; toggle(); });
      tr.addEventListener("keydown", (e) => { if (e.key === "Enter" || e.key === " ") { e.preventDefault(); toggle(); } });
    });

    // 주문건 개별 선택 체크박스
    body.querySelectorAll(".rowSelect").forEach(cb => {
      cb.addEventListener("click", (e) => e.stopPropagation()); // 행 펼침과 분리
      cb.addEventListener("change", () => {
        if (cb.checked) selected.add(cb.dataset.id); else selected.delete(cb.dataset.id);
        updateSelectionUI();
      });
    });

    body.querySelectorAll("[data-copy]").forEach(btn => {
      btn.addEventListener("click", () => {
        const r = DATA.find(x => x.requestId === btn.dataset.copy);
        if (r) copyText(slackText(r), btn);
      });
    });
    body.querySelectorAll("[data-del]").forEach(btn => {
      btn.addEventListener("click", () => {
        const id = btn.dataset.del;
        if (btn.dataset.armed !== "1") {
          btn.dataset.armed = "1";
          btn.textContent = "한 번 더 누르면 삭제";
          setTimeout(() => { if (btn.dataset.armed === "1") { btn.dataset.armed = ""; btn.textContent = "삭제"; } }, 4000);
          return;
        }
        LOCAL = LOCAL.filter(r => r.requestId !== id);
        saveLocal();
        expanded.delete(id);
        selected.delete(id);
        refreshAll();
      });
    });

    // 펼쳐 둔 상세는 표를 다시 그릴 때 내용이 비워집니다.
    // (30초마다 자동 새로고침이 돌아서 '불러오는 중…'에서 멈춰 있던 문제)
    // → 여기서 곧바로 다시 채웁니다. 이미 받아둔 내용은 캐시에서 즉시 복원돼 서버를 다시 부르지 않아요.
    expanded.forEach(id => {
      if (!body.querySelector(`tr[data-detail="${CSS.escape(id)}"]`)) return;
      loadAttachmentsInto(id);
      loadHistoryInto(id);
      loadSourcePreviewInto(id);
    });
    if (expanded.size) refreshLineStocks();

    updateSelectionUI();
  }

  function refreshAll() {
    rebuildData();
    renderStats();
    renderToday();
    renderTeamChips();
    renderAssigneeFilter();
    renderRequesterFilter();
    renderChannelFilter();
    renderDatalists();
    renderTable();
  }

  function setView(view) {
    activeView = view;
    selected.clear();   // 화면을 바꾸면 선택은 초기화
    document.querySelectorAll(".navitem").forEach(b => b.classList.toggle("active", b.dataset.view === view));
    clearTodayFilter();

    const titles = {
      dashboard: ["대시보드", "슬랙으로 들어온 업무 요청을 놓치지 않고 한눈에 확인해요"],
      all: ["전체 요청", "지금까지 들어온 업무 요청이에요. 취소된 건은 '취소 내역'에서 따로 볼 수 있어요"],
      done: ["완료 내역", "처리를 마친 업무 요청 목록이에요"],
      cancelled: ["취소 내역", "취소된 요청만 모아둔 곳이에요. 확인만 하면 되고, 다른 목록에는 표시되지 않아요"],
      safety: ["안전재고 요청", "재고 확보만을 위한 요청이에요. 출고요청 목록과는 따로 관리돼요"],
      form: ["업무 요청서 작성", "출고 요청 내용을 입력하면 대장부에 새 항목으로 등록할 수 있어요"],
      logistics: ["물류 대장", "물류팀 출고요청 전용 화면 — 재고 확인·D-day·보류까지 관리해요"],
      exceltool: ["엑셀 작업실", "엑셀 파일을 올려서 매칭·계산·정리 작업을 하고, 완료되면 그대로 내려받아요"],
    };
    document.getElementById("pageTitle").textContent = titles[view][0];
    document.getElementById("pageSub").textContent = titles[view][1];
    document.getElementById("tableTitle").textContent = view === "done" ? "완료된 요청" : (view === "cancelled" ? "취소된 요청" : (view === "safety" ? "안전재고 요청" : "최근 요청"));

    const isForm = view === "form";
    const isLogistics = view === "logistics";
    const isExcelTool = view === "exceltool";
    const isCancelled = view === "cancelled";
    const isSafety = view === "safety";
    const listOnly = isCancelled || isSafety; // 지표·차트·안내문 없이 목록만
    // ① 팀장님 피드백 — 목록 화면에서는 '업무 진행률(도넛)·오늘의 업무·사용법'을 감추고,
    //    계속 쌓이기만 하는 '전체 요청·완료' 카드도 전체 요청 화면에서는 숨깁니다.
    //    (대시보드 화면에서는 지금처럼 전부 보여요)
    document.body.classList.toggle("lean-banner", view !== "dashboard");
    document.body.classList.toggle("hide-cumulative", view === "all");
    document.querySelector(".stats").style.display = (isForm || isLogistics || isExcelTool || listOnly) ? "none" : "";
    document.querySelector(".panels").style.display = (isForm || isLogistics || isExcelTool || listOnly) ? "none" : "";
    document.querySelector(".tablecard").style.display = (isForm || isLogistics || isExcelTool) ? "none" : "";
    const searchPanelEl = document.getElementById("searchPanel");
    if (searchPanelEl) searchPanelEl.style.display = (isForm || isLogistics || isExcelTool) ? "none" : "";
    document.querySelector(".howto").style.display = (isForm || isLogistics || isExcelTool || listOnly) ? "none" : "";
    document.getElementById("formSection").style.display = isForm ? "" : "none";
    document.getElementById("logisticsSection").style.display = isLogistics ? "" : "none";
    document.getElementById("excelToolSection").style.display = isExcelTool ? "" : "none";
    // 상태 탭(대기/진행중/완료)은 취소 목록에서만 의미가 없으므로 숨깁니다.
    // 안전재고는 완료건을 따로 보고 싶다는 피드백으로 다른 화면처럼 탭을 그대로 씁니다.
    const statusTabsEl = document.getElementById("statusTabs");
    if (statusTabsEl) statusTabsEl.style.display = isCancelled ? "none" : "";
    // 화면을 옮길 때 상태 탭 기본값 — 취소·완료 목록은 '전체', 그 외(안전재고 포함)는 '진행 필요'
    const defStatus = (isCancelled || view === "done") ? "" : "open";
    activeStatus = defStatus;
    document.querySelectorAll(".tab").forEach(x => x.classList.toggle("active", x.dataset.status === defStatus));
    PAGE = 1;
    // 안전재고 화면: 수령자·수령처·송장 열 숨기고, '출고 희망일'을 '재고 확보 희망일'로 표기
    const reqTable = document.querySelector(".reqtable");
    if (reqTable) reqTable.classList.toggle("hide-supply", isSafety);
    const dueLbl = document.querySelector(".reqtable .dueLbl");
    if (dueLbl) dueLbl.textContent = isSafety ? "재고 확보 희망일" : "출고 희망일";
    // '선택 진행(다음 단계)' 버튼은 안전재고 화면에서만 보입니다. (대기→진행중→완료)
    const advBtn = document.getElementById("bulkAdvanceBtn");
    if (advBtn) advBtn.style.display = isSafety ? "" : "none";

    renderStats();   // 화면에 따라 감출 카드(0건 경고 등)를 다시 계산
    renderTable();
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  /* ---------- 요청서 폼 ---------- */
  let productSeq = 0;
  let billingValue = "";
  let formRequestType = "출고요청";

  // 요청 종류(출고요청/안전재고) 전환 — 안전재고는 판매처·유상무상·수령정보·발주서 섹션을 숨깁니다.
  function setReqType(type) {
    formRequestType = type || "출고요청";
    document.querySelectorAll("#reqTypeSeg .segbtn").forEach(b => {
      const on = b.dataset.reqtype === formRequestType;
      b.classList.toggle("sel", on);
      b.setAttribute("aria-pressed", String(on));
    });
    const safety = formRequestType === "안전재고";
    const hide = (el, h) => { if (el) el.style.display = h ? "none" : ""; };
    const billingField = document.getElementById("billingSeg");
    hide(billingField ? billingField.closest(".field") : null, safety);
    const channelField = document.getElementById("channelChecks");
    hide(channelField ? channelField.closest(".field") : null, safety);
    const rcv = document.getElementById("fRcvName");
    hide(rcv ? rcv.closest(".fcard") : null, safety);
    const ord = document.getElementById("fOrderFile");
    hide(ord ? ord.closest(".fcard") : null, safety);
    // 출고 희망일 라벨은 안전재고일 때 '희망일'로만 표기
    const dLabel = document.querySelector('label[for="fDesiredDate"]');
    if (dLabel) dLabel.childNodes[0].nodeValue = safety ? "재고 확보 희망일" : "출고 희망일";
    if (safety) billingValue = "free";
  }

  function buildChannelChecks() {
    const el = document.getElementById("channelChecks");
    el.innerHTML = CHANNELS.map((c, i) => `
      <label class="checkopt" for="ch${i}">
        <input type="checkbox" id="ch${i}" class="chBox" value="${esc(c)}">
        <span>${esc(c)}</span>
      </label>`).join("");
    el.querySelectorAll(".chBox").forEach(box => {
      box.addEventListener("change", () => {
        box.closest(".checkopt").classList.toggle("on", box.checked);
        if (box.value === "기타") {
          document.getElementById("channelOtherWrap").style.display = box.checked ? "" : "none";
          if (!box.checked) document.getElementById("fChannelOther").value = "";
          else document.getElementById("fChannelOther").focus();
        }
        el.classList.remove("invalid");
        const err = document.querySelector('[data-err="channels"]');
        if (err) err.textContent = "";
      });
    });
  }

  function selectedChannels() {
    return [...document.querySelectorAll(".chBox")].filter(b => b.checked).map(b => b.value);
  }

  function productRowHtml(i) {
    return `<div class="prow" data-prow="${i}">
      <div class="pidx num">#<span class="pn">${i}</span></div>
      <div class="field">
        <label for="pCode${i}" style="display:flex;align-items:center;gap:6px;">
          <input type="checkbox" class="pPick" data-prow-pick="${i}" title="이 상품을 일괄 수량 등록 대상으로 선택"
                 style="width:15px;height:15px;accent-color:var(--accent);cursor:pointer;margin:0;flex-shrink:0;">
          상품코드
        </label>
        <input id="pCode${i}" class="pCode" type="text" placeholder="예: PK-0032" autocomplete="off">
        <button type="button" class="btn ghost small stockCheckBtn" data-idx="${i}"
                style="margin-top:4px;">재고확인</button>
        <div class="pCodeWarn" style="display:none;margin-top:4px;font-size:11px;color:var(--warn);line-height:1.4;">⚠️ 코드를 비우면 물류대장에서 재고가 안 보여요. 오른쪽 재고검색에서 상품을 클릭하면 코드가 자동으로 채워져요.</div>
      </div>
      <div class="field">
        <label for="pName${i}">상품명<span class="req">*</span></label>
        <input id="pName${i}" class="pName" type="text" placeholder="예: 포장 박스 (중)" autocomplete="off">
      </div>
      <div class="field">
        <label for="pOpt${i}">옵션<span class="req">*</span></label>
        <input id="pOpt${i}" class="pOpt" type="text" placeholder="옵션 없으면 '없음' 입력" autocomplete="off">
      </div>
      <div class="field">
        <label for="pQty${i}">수량<span class="req">*</span></label>
        <input id="pQty${i}" class="pQty" type="number" min="1" step="1" inputmode="numeric" placeholder="0">
      </div>
      <button type="button" class="iconbtn del" title="이 상품 삭제" aria-label="이 상품 삭제">
        <svg class="icon" viewBox="0 0 24 24"><path d="M4 7h16M9 7V5a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2M6 7l1 12a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1l1-12"/></svg>
      </button>
    </div>`;
  }

  function syncProductRows() {
    const rows = [...document.querySelectorAll(".prow")];
    rows.forEach((row, idx) => {
      row.querySelector(".pn").textContent = idx + 1;
      row.querySelector(".del").disabled = rows.length <= 1;
    });
  }

  // 상품코드가 비어 있는데 상품명이 입력돼 있으면(=재고 조회가 안 되는 상태) 경고를 보여줍니다.
  function updateCodeWarn(row) {
    if (!row) return;
    const code = row.querySelector(".pCode").value.trim();
    const name = row.querySelector(".pName").value.trim();
    const warn = row.querySelector(".pCodeWarn");
    if (warn) warn.style.display = (!code && name) ? "block" : "none";
  }

  function addProductRow(focus) {
    productSeq += 1;
    const wrap = document.getElementById("productRows");
    wrap.insertAdjacentHTML("beforeend", productRowHtml(productSeq));
    const row = wrap.lastElementChild;
    row.querySelector(".del").addEventListener("click", () => {
      if (document.querySelectorAll(".prow").length <= 1) return;
      row.remove();
      syncProductRows();
    });
    // 상품코드/상품명을 고칠 때마다 경고 표시를 다시 계산합니다.
    row.querySelector(".pCode").addEventListener("input", () => updateCodeWarn(row));
    row.querySelector(".pName").addEventListener("input", () => updateCodeWarn(row));
    syncProductRows();
    if (focus) row.querySelector(".pName").focus();
  }

  function clearErrors() {
    document.querySelectorAll("[data-err]").forEach(el => { el.textContent = ""; });
    document.querySelectorAll("#formSection .invalid").forEach(el => el.classList.remove("invalid"));
    document.getElementById("billingSeg").classList.remove("invalid");
    document.getElementById("channelChecks").classList.remove("invalid");
  }
  function setErr(key, msg, inputId) {
    const el = document.querySelector(`[data-err="${key}"]`);
    if (el) el.textContent = msg;
    if (inputId) {
      const input = document.getElementById(inputId);
      if (input) input.classList.add("invalid");
    }
  }
  function showBanner(html, kind) {
    document.getElementById("formBanner").innerHTML = html
      ? `<div class="banner${kind ? " " + kind : ""}">${html}</div>` : "";
  }

  function nextRequestId() {
    const nums = DATA.map(r => /^SR-(\d+)$/.exec(r.requestId || ""))
      .filter(Boolean).map(m => parseInt(m[1], 10));
    const n = (nums.length ? Math.max(...nums) : 1000) + 1;
    return "SR-" + String(n).padStart(4, "0");
  }

  function readForm() {
    const val = (id) => document.getElementById(id).value.trim();
    const products = [...document.querySelectorAll(".prow")].map(row => ({
      code: row.querySelector(".pCode").value.trim(),
      name: row.querySelector(".pName").value.trim(),
      option: row.querySelector(".pOpt").value.trim(),
      qtyRaw: row.querySelector(".pQty").value.trim(),
      row,
    }));
    return {
      team: val("fTeam"),
      requester: val("fRequester"),
      assignee: val("fAssignee"),
      requestDate: val("fRequestDate"),
      desiredDate: val("fDesiredDate"),
      billing: billingValue,
      channels: selectedChannels(),
      channelOther: val("fChannelOther"),
      content: val("fContent"),
      receiver: {
        name: val("fRcvName"),
        phone: val("fRcvPhone"),
        address: val("fRcvAddr"),
        message: val("fRcvMsg"),
      },
      products,
    };
  }

  // 요청서 제출 시 함께 올릴 첨부파일. 발주서 자동채우기로 올린 원본도 여기에 담아 함께 첨부합니다.
  let orderFilesForAttach = [];
  // 여러 번에 걸쳐(따로따로) 올린 파일도 이전 것이 사라지지 않도록 쌓아두는 저장소.
  // resetForm() 등 초기화 코드보다 먼저 선언돼야 해서 위쪽에 둡니다.
  const fileStore = { fOrderFile: [], fAttachFiles: [], fBulkFile: [], fBulkRefFiles: [], fOrderRefFiles: [] };
  const fileKey = (f) => f.name + "|" + f.size + "|" + f.lastModified;

  /** 첨부파일 칸(fAttachFiles)의 파일 + 발주서 원본을 모아 서버에 업로드합니다. */
  async function uploadAttachments(sr) {
    if (!sr) return { ok: true, count: 0 };
    const input = document.getElementById("fAttachFiles");
    const picked = (input && input.files) ? Array.from(input.files) : [];
    const combined = picked.concat(orderFilesForAttach || []);
    // 같은 파일(이름+크기) 중복 제거
    const seen = new Set();
    const files = combined.filter(f => {
      const key = f.name + "|" + f.size;
      if (seen.has(key)) return false;
      seen.add(key);
      return true;
    });
    if (!files.length) return { ok: true, count: 0 };

    const fd = new FormData();
    files.forEach(f => fd.append("files", f));
    try {
      const res = await fetch(API_BASE + "/api/attachments/" + encodeURIComponent(sr), {
        method: "POST",
        body: fd,   // multipart/form-data — Content-Type은 브라우저가 자동 지정
      });
      if (!res.ok) throw new Error("서버 응답 " + res.status);
      return { ok: true, count: files.length };
    } catch (e) {
      return { ok: false, count: files.length, error: e.message || "연결 오류" };
    }
  }

  async function submitForm() {
    clearErrors();
    const f = readForm();
    let firstBad = null;
    const fail = (key, msg, inputId) => { setErr(key, msg, inputId); if (!firstBad) firstBad = inputId || key; };

    if (!f.team) fail("fTeam", "요청팀을 입력해 주세요", "fTeam");
    if (!f.requester) fail("fRequester", "요청자를 입력해 주세요", "fRequester");
    if (!f.requestDate) fail("fRequestDate", "요청일자를 선택해 주세요", "fRequestDate");
    if (!f.desiredDate) fail("fDesiredDate", "출고 희망일을 선택해 주세요", "fDesiredDate");
    else if (f.requestDate && f.desiredDate < f.requestDate) fail("fDesiredDate", "출고 희망일이 요청일자보다 빨라요", "fDesiredDate");
    // 안전재고(재고확보) 요청은 판매처·유상무상·수령정보가 필요 없어요. 출고요청일 때만 검증합니다.
    const isSafety = (formRequestType === "안전재고");
    if (!isSafety) {
      if (!f.billing) { setErr("billing", "유상 또는 무상을 선택해 주세요"); document.getElementById("billingSeg").classList.add("invalid"); if (!firstBad) firstBad = "billingSeg"; }
      if (!f.channels.length) {
        setErr("channels", "판매처를 하나 이상 선택해 주세요");
        document.getElementById("channelChecks").classList.add("invalid");
        if (!firstBad) firstBad = "channelChecks";
      } else if (f.channels.includes("기타") && !f.channelOther) {
        setErr("channels", "기타를 선택했어요 — 판매처 이름을 입력해 주세요", "fChannelOther");
        if (!firstBad) firstBad = "fChannelOther";
      }
      if (!f.receiver.name) fail("fRcvName", "수령자 이름을 입력해 주세요", "fRcvName");
      if (!f.receiver.phone) fail("fRcvPhone", "수령자 연락처를 입력해 주세요", "fRcvPhone");
      if (!f.receiver.address) fail("fRcvAddr", "수령자 주소를 입력해 주세요", "fRcvAddr");
    }

    const filled = f.products.filter(p => p.code || p.name || p.option || p.qtyRaw);
    const target = filled.length ? filled : f.products.slice(0, 1);
    let productErr = "";
    target.forEach(p => {
      if (!p.name) { productErr = "상품명은 모든 상품 줄에 입력해야 해요"; p.row.querySelector(".pName").classList.add("invalid"); }
      if (!p.option) { productErr = "옵션도 모든 상품 줄에 입력해야 해요. (옵션이 없는 상품이면 '없음'이라고 적어 주세요)"; p.row.querySelector(".pOpt").classList.add("invalid"); }
      const q = Number(p.qtyRaw);
      if (!p.qtyRaw || !Number.isFinite(q) || q <= 0) { productErr = "수량은 1 이상의 숫자로 입력해 주세요"; p.row.querySelector(".pQty").classList.add("invalid"); }
    });
    if (!filled.length) productErr = "상품을 1개 이상 입력해 주세요";
    if (productErr) { setErr("products", productErr); if (!firstBad) firstBad = "productRows"; }

    if (firstBad) {
      showBanner("입력하지 않은 항목이 있어요. 빨간색으로 표시된 곳을 확인해 주세요.", "error");
      const el = document.getElementById(firstBad);
      if (el) { el.scrollIntoView({ behavior: "smooth", block: "center" }); if (el.focus) el.focus({ preventScroll: true }); }
      return;
    }

    const products = target.map(p => ({ code: p.code, name: p.name, option: p.option, qty: Number(p.qtyRaw) }));

    const channelStr = f.channels
      .map(c => (c === "기타" && f.channelOther) ? ("기타:" + f.channelOther) : c)
      .join(",");

    const payload = {
      requestTeam: f.team,
      requester: f.requester,
      assignee: f.assignee || null,
      itemName: products[0].name,
      sku: products[0].code || null,
      optionValue: products[0].option || null,
      quantity: products.reduce((a, p) => a + p.qty, 0),
      wantDate: f.desiredDate,
      receivePlace: isSafety ? null : (f.receiver.address || null),
      note: f.content || null,
      scope: "team",
      channels: isSafety ? "" : channelStr,
      productsJson: JSON.stringify(products),
      receiverName: isSafety ? null : (f.receiver.name || null),
      receiverPhone: isSafety ? null : (f.receiver.phone || null),
      receiverAddress: isSafety ? null : (f.receiver.address || null),
      receiverMessage: isSafety ? null : (f.receiver.message || null),
      billingType: isSafety ? "free" : f.billing,
      slackChannelId: editingSrNo ? undefined : originSlackChannel,
      slackUserId: editingSrNo ? undefined : originSlackUser,
    };

    const submitBtn = document.getElementById("submitFormBtn");
    const btnLabel = submitBtn ? submitBtn.textContent : "";
    const isEdit = !!editingSrNo;

    // 수정일 때는 변경자·사유를 입력받아 변경 이력에 남깁니다.
    let changeQS = "";
    if (isEdit) {
      const info = await askChange(editingSrNo + " 요청 수정");
      if (!info) return;   // 사용자가 닫음 → 폼 그대로 유지
      changeQS = "?changedBy=" + encodeURIComponent(info.actor) + "&changeReason=" + encodeURIComponent(info.reason);
    }

    if (submitBtn) { submitBtn.disabled = true; submitBtn.textContent = isEdit ? "수정 저장 중..." : "저장 중..."; }

    let created;
    try {
      const url = isEdit ? (API_CREATE + "/" + encodeURIComponent(editingSrNo) + changeQS) : API_CREATE;
      const res = await fetch(url, {
        method: isEdit ? "PATCH" : "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
      if (!res.ok) {
        const text = await res.text().catch(() => "");
        throw new Error("서버 응답 " + res.status + (text ? " — " + text.slice(0, 200) : ""));
      }
      created = await res.json();
    } catch (e) {
      if (submitBtn) { submitBtn.disabled = false; submitBtn.textContent = btnLabel; }
      showBanner(
        '<span><strong>저장하지 못했어요.</strong> 입력한 내용은 그대로 남아 있으니 다시 시도해 주세요.<br>'
        + '<span style="opacity:.75;font-size:13px;">' + esc(e.message || "연결 실패") + '</span></span>',
        "error"
      );
      document.getElementById("formBanner").scrollIntoView({ behavior: "smooth", block: "nearest" });
      return;
    }

    if (submitBtn) { submitBtn.disabled = false; submitBtn.textContent = btnLabel; }

    const entry = { requestId: (created && created.sr) ? created.sr : (editingSrNo || "") };
    const wasEdit = isEdit;
    editingSrNo = null;

    // 안전재고 요청이면 접수 직후 요청 유형을 저장합니다. (출고요청은 서버 기본값이라 호출 불필요)
    if (!wasEdit && isSafety && entry.requestId) {
      try {
        await fetch(API_CREATE + "/" + encodeURIComponent(entry.requestId) + "/type", {
          method: "PATCH", headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ type: "안전재고" })
        });
      } catch (e) { /* 유형 저장 실패해도 접수 자체는 완료됨 */ }
    }

    // 첨부파일 업로드 — 요청서가 만들어진 뒤(sr 확보 후)에 올립니다.
    const attachRes = await uploadAttachments(entry.requestId);

    await fetchFromServer();

    if (attachRes && attachRes.count > 0 && !attachRes.ok) {
      alert("요청서는 정상 등록됐어요. 다만 첨부파일 업로드에 실패했어요.\n네트워크 상태를 확인하고, 잠시 후 대장부에서 다시 시도해 주세요.");
    }

    if (wasEdit) {
      resetForm(true);
      showBanner(`<span>${esc(entry.requestId)} 요청서를 수정했어요.</span>`, "");
      return;
    }

    // 신규 등록 완료 — 곧바로 폼을 비우고 전체요청 화면으로 이동합니다.
    // (예전엔 화면 가운데 팝업만 띄우고 폼은 그대로 남겨 뒀는데, 팝업을 배경 클릭으로 닫으면
    //  입력값이 그대로 남아 제출 버튼을 다시 누르면 같은 내용으로 요청서가 하나 더 만들어지는
    //  이중등록 사고로 이어졌습니다. 폼을 확실히 비우고 화면을 옮겨서 막습니다.)
    resetForm(false);
    expanded.add(entry.requestId);
    setView("all");
    showPostSubmitBanner(entry.requestId);
  }

  /**
   * 신규 등록 직후 전체요청 화면에 띄우는 안내 배너.
   * 방금 만든 요청으로 스크롤해 보여주고, 같은 조건(요청팀·요청자·일정·유상무상·판매처)으로
   * 이어서 등록하고 싶으면 링크로 폼을 다시 열 수 있게 합니다 — 예전 팝업의 '주문서 유지'와 동일한 결과지만,
   * 아무 것도 안 누르고 넘어가도 폼이 비어 있으니 이중등록 위험이 없습니다.
   */
  function showPostSubmitBanner(sr) {
    showBanner(
      `<span>✅ <strong>${esc(sr)}</strong> 요청서가 등록됐어요. `
      + `<a href="#" id="continueSameOrderLink" style="color:var(--accent);text-decoration:underline;font-weight:700;">같은 조건으로 이어서 작성</a>`
      + ` · <a href="#" id="copyJustSubmittedLink" style="color:var(--accent);text-decoration:underline;font-weight:700;">슬랙용 텍스트 복사</a></span>`,
      "ok"
    );
    const contLink = document.getElementById("continueSameOrderLink");
    if (contLink) contLink.addEventListener("click", (e) => {
      e.preventDefault();
      const r = DATA.find(x => x.requestId === sr);
      if (r) {
        prefillFormForEdit(r);
        editingSrNo = null;
        resetOrderOnly();   // 요청팀·요청자·일정·유상무상·판매처는 남기고 상품·수령정보만 비웁니다.
        const submitBtn = document.getElementById("submitFormBtn");
        if (submitBtn) submitBtn.textContent = "요청서 작성";
        showBanner(`<span>📋 <strong>주문서를 유지했어요.</strong> 요청팀·요청자·일정은 그대로 두었으니, <strong>상품과 수령정보만</strong> 입력해서 다음 분께 이어서 등록하세요.</span>`, "ok");
      } else {
        setView("form");
      }
      const firstName = document.querySelector("#productRows .pName");
      if (firstName) { try { firstName.focus(); } catch (e2) {} }
    });
    const copyLink = document.getElementById("copyJustSubmittedLink");
    if (copyLink) copyLink.addEventListener("click", (e) => {
      e.preventDefault();
      const r = DATA.find(x => x.requestId === sr);
      if (r) copyText(slackText(r), copyLink);
    });
    setTimeout(() => {
      const row = document.querySelector(`tr.mainrow[data-id="${CSS.escape(sr)}"]`);
      if (row) row.scrollIntoView({ behavior: "smooth", block: "center" });
    }, 150);
  }

  function resetForm(keepBanner) {
    clearErrors();
    ["fTeam", "fRequester", "fAssignee", "fContent", "fRcvName", "fRcvPhone", "fRcvAddr", "fRcvMsg"]
      .forEach(id => { document.getElementById(id).value = ""; });
    document.getElementById("fRequestDate").value = TODAY;
    document.getElementById("fDesiredDate").value = "";
    billingValue = "";
    document.querySelectorAll("#billingSeg .segbtn").forEach(b => { b.classList.remove("sel"); b.setAttribute("aria-pressed", "false"); });
    document.querySelectorAll(".chBox").forEach(b => { b.checked = false; b.closest(".checkopt").classList.remove("on"); });
    document.getElementById("fChannelOther").value = "";
    document.getElementById("channelOtherWrap").style.display = "none";
    document.getElementById("productRows").innerHTML = "";
    productSeq = 0;
    addProductRow(false);
    if (typeof setReqType === "function") setReqType("출고요청");
    // 첨부파일 관련 상태 초기화
    orderFilesForAttach = [];
    fileStore.fAttachFiles = [];
    const af = document.getElementById("fAttachFiles"); if (af) af.value = "";
    const al = document.getElementById("fAttachList"); if (al) al.textContent = "";
    fileStore.fOrderFile = [];
    const of = document.getElementById("fOrderFile"); if (of) of.value = "";
    const ofn = document.getElementById("fOrderFileName"); if (ofn) ofn.textContent = "";
    fileStore.fOrderRefFiles = [];
    const orf = document.getElementById("fOrderRefFiles"); if (orf) orf.value = "";
    const orfn = document.getElementById("fOrderRefFilesName"); if (orfn) orfn.textContent = "";
    fileStore.fBulkFile = [];
    const bf = document.getElementById("fBulkFile"); if (bf) bf.value = "";
    const bfn = document.getElementById("fBulkFileName"); if (bfn) bfn.textContent = "";
    fileStore.fBulkRefFiles = [];
    const brf = document.getElementById("fBulkRefFiles"); if (brf) brf.value = "";
    const brfn = document.getElementById("fBulkRefFilesName"); if (brfn) brfn.textContent = "";
    const bm = document.getElementById("fBulkMsg"); if (bm) bm.textContent = "";
    if (!keepBanner) showBanner("");
  }

  /** '동일 주문건 추가' — 요청팀·요청자·요청일자·출고희망일·유상무상·판매처는 그대로 두고,
   *  상품·수령정보·요청내용·첨부만 비웁니다. (같은 조건으로 여러 명에게 연속 발송할 때 편하게) */
  function resetOrderOnly() {
    clearErrors();
    ["fContent", "fRcvName", "fRcvPhone", "fRcvAddr", "fRcvMsg"]
      .forEach(id => { const el = document.getElementById(id); if (el) el.value = ""; });
    document.getElementById("productRows").innerHTML = "";
    productSeq = 0;
    addProductRow(false);
    orderFilesForAttach = [];
    fileStore.fAttachFiles = [];
    const af = document.getElementById("fAttachFiles"); if (af) af.value = "";
    const al = document.getElementById("fAttachList"); if (al) al.textContent = "";
    fileStore.fOrderFile = [];
    const of = document.getElementById("fOrderFile"); if (of) of.value = "";
    const ofn = document.getElementById("fOrderFileName"); if (ofn) ofn.textContent = "";
    fileStore.fOrderRefFiles = [];
    const orf = document.getElementById("fOrderRefFiles"); if (orf) orf.value = "";
    const orfn = document.getElementById("fOrderRefFilesName"); if (orfn) orfn.textContent = "";
    fileStore.fBulkFile = [];
    const bf = document.getElementById("fBulkFile"); if (bf) bf.value = "";
    const bfn = document.getElementById("fBulkFileName"); if (bfn) bfn.textContent = "";
    fileStore.fBulkRefFiles = [];
    const brf = document.getElementById("fBulkRefFiles"); if (brf) brf.value = "";
    const brfn = document.getElementById("fBulkRefFilesName"); if (brfn) brfn.textContent = "";
    const bm = document.getElementById("fBulkMsg"); if (bm) bm.textContent = "";
  }

  /** 대장부 표의 '수정' 버튼 — 요청서 작성 화면을 그 요청 내용으로 채워서 엽니다. */
  function prefillFormForEdit(r) {
    editingSrNo = r.requestId;
    resetForm(true);
    setView("form");

    document.getElementById("fTeam").value = r.team || "";
    document.getElementById("fRequester").value = r.requester || "";
    document.getElementById("fAssignee").value = r.assignee || "";
    document.getElementById("fContent").value = r.content || "";
    document.getElementById("fRequestDate").value = r.requestDate || TODAY;
    document.getElementById("fDesiredDate").value = r.desiredDate || "";

    billingValue = r.billing || "";
    document.querySelectorAll("#billingSeg .segbtn").forEach(b => {
      const sel = b.dataset.val === billingValue;
      b.classList.toggle("sel", sel);
      b.setAttribute("aria-pressed", sel ? "true" : "false");
    });

    document.querySelectorAll(".chBox").forEach(b => {
      const on = r.channels.includes(b.value);
      b.checked = on;
      const opt = b.closest(".checkopt");
      if (opt) opt.classList.toggle("on", on);
    });
    if (r.channelOther) {
      document.getElementById("fChannelOther").value = r.channelOther;
      document.getElementById("channelOtherWrap").style.display = "";
      const otherBox = [...document.querySelectorAll(".chBox")].find(b => b.value === "기타");
      if (otherBox) { otherBox.checked = true; otherBox.closest(".checkopt")?.classList.add("on"); }
    }

    document.getElementById("fRcvName").value = r.receiver.name || "";
    document.getElementById("fRcvPhone").value = r.receiver.phone || "";
    document.getElementById("fRcvAddr").value = r.receiver.address || "";
    document.getElementById("fRcvMsg").value = r.receiver.message || "";

    document.getElementById("productRows").innerHTML = "";
    productSeq = 0;
    if (r.products.length) {
      r.products.forEach(p => {
        addProductRow(false);
        const row = document.querySelector("#productRows .prow:last-child");
        row.querySelector(".pCode").value = p.code || "";
        row.querySelector(".pName").value = p.name || "";
        row.querySelector(".pOpt").value = p.option || "";
        row.querySelector(".pQty").value = p.qty ?? "";
      });
    } else {
      addProductRow(false);
    }

    const submitBtn = document.getElementById("submitFormBtn");
    if (submitBtn) submitBtn.textContent = "수정 저장";

    showBanner(`<span>${esc(r.requestId)} 요청을 수정하는 중이에요. 내용을 고치고 다시 제출해 주세요.</span>`, "warn");
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  /**
   * '선택 복사' — 기존 요청서 내용(요청팀·요청자·판매처·유상무상·수령정보·상품)을 그대로 옮겨
   * '새 요청서' 로 작성 화면을 채웁니다. 올리브영처럼 정기적으로 같은 곳으로 나가는 발주는
   * 주소는 그대로 두고 상품·수량만 고쳐서 바로 제출할 수 있어요.
   * prefillFormForEdit 과 달리 editingSrNo 를 세팅하지 않으므로, 제출하면 PATCH 가 아니라
   * 완전히 새로운 요청서(POST)로 등록됩니다.
   */
  function prefillFormForCopy(r) {
    prefillFormForEdit(r);       // 값 채우기 로직은 수정과 동일 — 그대로 재사용
    editingSrNo = null;           // 다만 '이 요청을 고치는 중'은 아니고, 새 요청서를 만드는 것입니다.
    const submitBtn = document.getElementById("submitFormBtn");
    if (submitBtn) submitBtn.textContent = "요청서 작성";
    showBanner(`<span>📄 <strong>${esc(r.requestId)}</strong> 내용을 복사했어요. 상품·수량을 확인하고 <strong>새 요청서</strong>로 제출해 주세요. (원본 요청서는 그대로 남아 있어요)</span>`, "ok");
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  document.getElementById("bulkCopyBtn").addEventListener("click", () => {
    const ids = [...selected];
    if (ids.length === 0) { alert("먼저 왼쪽 체크박스로 복사할 요청 1건을 골라 주세요."); return; }
    if (ids.length > 1) { alert("복사는 한 번에 한 건만 할 수 있어요. 요청 1건만 선택해 주세요."); return; }
    const r = DATA.find(x => x.requestId === ids[0]);
    if (!r) { alert("선택한 요청을 찾을 수 없어요. 새로 고침 후 다시 시도해 주세요."); return; }
    selected.clear(); updateSelectionUI();
    prefillFormForCopy(r);
  });

  /** 대장부 표의 '취소' 버튼 — 변경자 이름·사유를 입력받아 변경 이력에 남깁니다. */
  async function cancelRequest(srNo) {
    const info = await askChange(srNo + " 요청 취소");
    if (!info) return;   // 사용자가 닫음
    const qs = "?changedBy=" + encodeURIComponent(info.actor) + "&changeReason=" + encodeURIComponent(info.reason);
    try {
      const res = await fetch(API_CREATE + "/" + encodeURIComponent(srNo) + "/cancel" + qs, { method: "PATCH" });
      if (!res.ok) throw new Error("서버 응답 " + res.status);
      await fetchFromServer();
    } catch (e) {
      alert(srNo + " 취소에 실패했어요: " + (e.message || "연결 오류"));
    }
  }

  /** 안전재고 화면의 상태 전환 버튼 — 대기 → 진행중 → 완료 → (다시)대기 순으로 넘어갑니다.
   *  물류대장(myorder)과 같은 /status API를 그대로 사용해서 백엔드 수정 없이 상태를 바꿔요. */
  async function setSafetyStatus(srNo, curStatus) {
    const nextKo = SAFETY_NEXT[curStatus] || "진행중";   // 서버는 한글 상태를 받습니다
    try {
      const res = await fetch(API_CREATE + "/" + encodeURIComponent(srNo) + "/status", {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ status: nextKo }),
      });
      if (!res.ok) throw new Error("서버 응답 " + res.status);
      await fetchFromServer();
    } catch (e) {
      alert(srNo + " 상태 변경에 실패했어요: " + (e.message || "연결 오류"));
    }
  }

  /** 대장부 상세보기의 '연결' 버튼 — 관리번호만 넘기면, 나머지는 백그라운드에서 자동 처리됩니다. */
  async function linkOrder(srNo, seq) {
    if (!seq.trim()) { alert("이지어드민 관리번호를 입력해 주세요."); return; }
    try {
      const res = await fetch(API_CREATE + "/" + encodeURIComponent(srNo) + "/link-order", {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ seq: seq.trim() }),
      });
      if (!res.ok) throw new Error("서버 응답 " + res.status);
      await fetchFromServer();
      alert(srNo + " 관리번호를 연결했어요. 송장이 등록되면 자동으로 채워지고 요청자에게도 자동으로 알림이 가요.");
    } catch (e) {
      alert(srNo + " 연결에 실패했어요: " + (e.message || "연결 오류"));
    }
  }

  // 표의 수정/취소/관리번호연결 버튼 — 이벤트 위임으로 한 번에 처리
  document.getElementById("tableBody").addEventListener("click", (e) => {
    const editBtn = e.target.closest(".rowEditBtn");
    if (editBtn) {
      const r = DATA.find(x => x.requestId === editBtn.dataset.id);
      if (r) prefillFormForEdit(r);
      return;
    }
    const cancelBtn = e.target.closest(".rowCancelBtn");
    if (cancelBtn) { cancelRequest(cancelBtn.dataset.id); return; }

    const safetyBtn = e.target.closest(".safetyStatusBtn");
    if (safetyBtn) { setSafetyStatus(safetyBtn.dataset.id, safetyBtn.dataset.status); return; }

    const memoBtn = e.target.closest(".rowMemoBtn");
    if (memoBtn) { openMemo(memoBtn.dataset.id); return; }

    const memoEdit = e.target.closest("[data-memo]");
    if (memoEdit) { e.stopPropagation(); openMemo(memoEdit.dataset.memo); return; }

    const linkBtn = e.target.closest(".linkOrderBtn");
    if (linkBtn) {
      const id = linkBtn.dataset.id;
      const input = document.querySelector(`.linkSeqInput[data-id="${CSS.escape(id)}"]`);
      const seq = input ? input.value : prompt("이지어드민 관리번호를 입력해 주세요");
      if (seq != null) linkOrder(id, seq);
    }
  });

  // [물류 메모] 요청건마다 특이사항(발주 진행상황 등)을 수기로 적어 저장 → 다른 팀도 볼 수 있게 서버에 보관
  async function openMemo(sr){
    const r = DATA.find(x => x.requestId === sr);
    if(!r) return;
    let ov = document.getElementById("memoModal");
    if(!ov){
      ov = document.createElement("div");
      ov.id = "memoModal";
      ov.style.cssText = "position:fixed;inset:0;background:rgba(0,0,0,.45);display:flex;align-items:center;justify-content:center;z-index:9999;padding:16px;";
      ov.innerHTML = `
        <div style="background:var(--surface);border:1px solid var(--border);border-radius:14px;max-width:460px;width:100%;padding:20px;box-shadow:0 12px 44px rgba(0,0,0,.22);">
          <div id="memoTitle" style="font-size:16px;font-weight:700;margin-bottom:4px;color:var(--text);"></div>
          <div style="font-size:13px;color:var(--text-muted);margin-bottom:12px;line-height:1.5;">발주 진행상황·특이사항을 적어두면 물류팀과 타팀이 함께 보고 진행 상황을 확인할 수 있어요.</div>
          <textarea id="memoText" rows="6" placeholder="예: 8/20 발주 예정 / 비시즌이라 입고 지연 예상 / 공장 재고 확인 중" style="width:100%;box-sizing:border-box;padding:10px;border:1px solid var(--border);border-radius:8px;font-size:14px;background:var(--surface-alt,#fff);color:var(--text);resize:vertical;font-family:inherit;line-height:1.5;"></textarea>
          <div id="memoErr" style="font-size:12px;color:var(--danger);min-height:16px;margin:6px 0;"></div>
          <div style="display:flex;gap:8px;justify-content:flex-end;">
            <button id="memoCancel" type="button" class="btn ghost">닫기</button>
            <button id="memoSave" type="button" class="btn primary">저장</button>
          </div>
        </div>`;
      document.body.appendChild(ov);
      ov.addEventListener("click", e => { if(e.target.id === "memoModal") ov.style.display = "none"; });
      document.addEventListener("keydown", e => { if(e.key === "Escape" && ov.style.display !== "none") ov.style.display = "none"; });
    }
    ov.querySelector("#memoTitle").textContent = sr + " · 특이사항 메모";
    const ta = ov.querySelector("#memoText");
    ta.value = r.memo || "";
    ov.querySelector("#memoErr").textContent = "";
    ov.style.display = "flex";
    setTimeout(() => ta.focus(), 30);
    const saveBtn = ov.querySelector("#memoSave");
    const cancelBtn = ov.querySelector("#memoCancel");
    cancelBtn.onclick = () => { ov.style.display = "none"; };
    saveBtn.onclick = async () => {
      const memo = ta.value;
      saveBtn.disabled = true; const old = saveBtn.textContent; saveBtn.textContent = "저장 중...";
      try{
        const res = await fetch(API_CREATE + "/" + encodeURIComponent(sr) + "/memo", {
          method: "PATCH", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ memo })
        });
        if(!res.ok) throw new Error("서버 응답 " + res.status);
        r.memo = memo;            // 화면에 바로 반영
        ov.style.display = "none";
        renderTable();
      }catch(err){
        ov.querySelector("#memoErr").textContent = "저장 실패: " + (err.message || "연결 오류");
      }finally{ saveBtn.disabled = false; saveBtn.textContent = old; }
    };
  }

  // ===== 물류팀 변경 알림 (CS창 스타일) =====
  // 이미 진행중/완료인 건을 요청자가 수정·취소하면 여기에 뜹니다. 직접 '확인'을 눌러야 사라집니다.
  const seenAlertIds = new Set();

  async function pollAlerts() {
    try {
      const res = await fetch(API_BASE + "/api/requests/alerts");
      if (!res.ok) return;
      const list = await res.json();
      renderAlertPanel(list);
    } catch (e) { /* 조용히 무시 — 다음 주기에 다시 시도 */ }
  }

  function renderAlertPanel(list) {
    let panel = document.getElementById("alertPanel");
    if (!panel) {
      panel = document.createElement("div");
      panel.id = "alertPanel";
      panel.style.cssText = "position:fixed;right:20px;bottom:20px;width:320px;max-height:70vh;overflow-y:auto;z-index:9999;display:flex;flex-direction:column;gap:8px;";
      document.body.appendChild(panel);
    }
    if (!list.length) { panel.innerHTML = ""; return; }

    panel.innerHTML = list.map(a => {
      const label = a.status === "cancelled" ? "취소" : "수정";
      return `<div class="alertcard" style="background:var(--surface);border:1px solid var(--danger);
                  border-radius:10px;padding:12px 14px;box-shadow:var(--shadow);">
        <div style="font-weight:700;color:var(--danger);font-size:13px;margin-bottom:4px;">⚠ 요청 변경 알림</div>
        <div style="font-size:13px;line-height:1.5;">
          <strong>${esc(a.requestId)}</strong> (${esc(a.team)} · ${esc(a.requester)})<br>
          이미 처리 중이던 요청을 <strong>${label}</strong>했어요.
        </div>
        <div style="margin-top:8px;text-align:right;">
          <button type="button" class="btn small alertAckBtn" data-id="${esc(a.requestId)}">확인</button>
        </div>
      </div>`;
    }).join("");

    panel.querySelectorAll(".alertAckBtn").forEach(btn => {
      btn.addEventListener("click", async () => {
        const id = btn.dataset.id;
        btn.disabled = true;
        try {
          await fetch(API_BASE + "/api/requests/" + encodeURIComponent(id) + "/alerts/ack", { method: "POST" });
        } catch (e) { /* 실패해도 다음 조회 때 다시 뜸 */ }
        pollAlerts();
      });
    });
  }

  // ===== 이지어드민 재고 검색 배너 (요청서 작성 화면에 항상 떠 있는 패널) =====
  (function setupStockSearch(){
    const input = document.getElementById("stockSearchInput");
    const results = document.getElementById("stockSearchResults");
    if (!input || !results) return;

    const refreshBtn = document.getElementById("stockSearchRefreshBtn");
    refreshBtn.addEventListener("click", async () => {
      refreshBtn.disabled = true;
      const original = refreshBtn.textContent;
      refreshBtn.textContent = "⏳";
      try {
        await fetch(API_BASE + "/api/stock/refresh", { method: "POST" });
        const q = input.value.trim();
        if (q) await runStockSearch(q);
      } catch (e) { /* 실패해도 기존 캐시로 계속 검색 가능 */ }
      refreshBtn.disabled = false;
      refreshBtn.textContent = original;
    });

    let debounceTimer = null;
    input.addEventListener("input", () => {
      clearTimeout(debounceTimer);
      const q = input.value.trim();
      if (!q) {
        results.innerHTML = '<div style="padding:20px 16px;color:var(--text-muted);text-align:center;">상품코드나 이름을 입력하면<br>여기에 재고가 바로 보여요.</div>';
        return;
      }
      results.innerHTML = '<div style="padding:14px 16px;color:var(--text-muted);">검색 중...</div>';
      debounceTimer = setTimeout(() => runStockSearch(q), 350);
    });

    let searchSeq = 0;
    async function runStockSearch(q){
      const seq = ++searchSeq;
      try {
        // 1단계: 이름·코드만 즉시 받아와 바로 표시 (재고는 아직 '조회중…')
        const res = await fetch(API_BASE + "/api/stock/search?q=" + encodeURIComponent(q) + "&withStock=false");
        if (!res.ok) throw new Error("서버 응답 " + res.status);
        const list = await res.json();
        if (seq !== searchSeq) return;   // 더 최신 검색이 있으면 이 결과는 버림
        renderResults(list);
        fillStock(list, seq);            // 2단계: 재고 숫자는 곧바로 따로 채움 (검색을 막지 않음)
      } catch (e) {
        if (seq === searchSeq) results.innerHTML = '<div style="padding:14px 16px;color:var(--danger);">검색에 실패했어요.</div>';
      }
    }

    // 2단계 — 방금 표시한 상품들의 실시간 재고를 받아와 '조회중…' 자리를 실제 숫자로 채웁니다.
    async function fillStock(list, seq){
      const codes = list.map(p => p.productId).filter(Boolean);
      if (!codes.length) return;
      try {
        const res = await fetch(API_BASE + "/api/stock?codes=" + encodeURIComponent(codes.join(",")));
        if (!res.ok) return;
        const map = await res.json();
        if (seq !== searchSeq) return;   // 이미 다른 검색으로 넘어갔으면 건드리지 않음
        results.querySelectorAll(".stkVal").forEach(el => {
          const s = map[el.dataset.code];
          if (s && (s.available != null || s.stock != null)) {
            const avail = s.available != null ? s.available : s.stock;
            el.textContent = s.available != null ? ("가용 " + avail) : ("재고 " + s.stock);
            el.style.color = (avail <= 0) ? 'var(--danger)' : 'var(--accent)';
          } else {
            el.textContent = "재고 없음";
            el.style.color = 'var(--text-muted)';
          }
        });
      } catch (e) { /* 재고만 못 채운 것 — 이름·코드는 이미 떠 있음 */ }
    }

    function renderResults(list){
      if (!list.length) {
        results.innerHTML = '<div style="padding:14px 16px;color:var(--text-muted);">일치하는 상품이 없어요.</div>';
        return;
      }
      results.innerHTML = list.map(p => {
        const has = (p.available != null || p.stock != null);
        const stockText = has ? (p.available != null ? `가용 ${p.available}` : `재고 ${p.stock}`) : "조회중…";
        const val = p.available != null ? p.available : p.stock;
        const stockColor = has ? ((val <= 0) ? 'var(--danger)' : 'var(--accent)') : 'var(--text-muted)';
        const optText = (p.option != null && String(p.option).trim()) ? String(p.option).trim() : "";
        return `<div class="stockResultRow" data-code="${esc(p.productId)}" data-name="${esc(p.name)}" data-option="${esc(optText)}"
             style="padding:10px 16px;border-bottom:1px solid var(--border);cursor:pointer;">
          <div style="font-weight:600;line-height:1.45;overflow-wrap:anywhere;word-break:break-word;">${esc(p.name)}</div>
          ${optText ? `<div style="font-size:12px;color:var(--text-muted);margin-top:2px;overflow-wrap:anywhere;">옵션: ${esc(optText)}</div>` : ""}
          <div style="display:flex;flex-wrap:wrap;align-items:center;gap:4px 12px;color:var(--text-muted);font-size:12px;margin-top:4px;">
            <span>${esc(p.productId)}</span>
            <span class="stkVal" data-code="${esc(p.productId)}" style="margin-left:auto;color:${stockColor};font-weight:700;white-space:nowrap;">${stockText}</span>
          </div>
        </div>`;
      }).join("");

      results.querySelectorAll(".stockResultRow").forEach(row => {
        row.addEventListener("click", () => {
          const code = row.dataset.code, name = row.dataset.name, option = row.dataset.option || "";
          // 마지막 상품 줄이 비어 있으면 거기에 채우고, 아니면 새 줄을 추가합니다.
          const rows = document.querySelectorAll("#productRows .prow");
          let target = rows[rows.length - 1];
          if (!target || target.querySelector(".pName").value.trim()) {
            addProductRow(false);
            target = document.querySelector("#productRows .prow:last-child");
          }
          target.querySelector(".pCode").value = code;
          target.querySelector(".pName").value = name;
          // 옵션이 있으면 옵션란에 자동 분리 입력, 없으면 '없음'으로 채워 별도 입력 안 해도 되게.
          target.querySelector(".pOpt").value = option || "없음";
          updateCodeWarn(target);
          // 상품을 추가한 뒤 커서를 '검색창'으로 되돌립니다.
          // (수량칸은 type=number라 포커스되면 한글 IME가 영문으로 꺼지는 문제가 있어 자동 포커스하지 않아요.
          //  검색창은 텍스트칸이라 한글 입력이 그대로 유지됩니다.)
          input.focus();
          input.select();
        });
      });
    }
  })();

  pollAlerts();
  setInterval(pollAlerts, 10000);

  /* ---------- 이벤트 ---------- */
  document.querySelectorAll(".navitem").forEach(b => b.addEventListener("click", () => setView(b.dataset.view)));
  // 왼쪽 위 '업무 요청 대장부' 로고 — 눌러서 대시보드(메인 화면)로 바로 이동
  document.getElementById("brandHome").addEventListener("click", () => setView("dashboard"));
  document.getElementById("newRequestBtn").addEventListener("click", () => setView("form"));

  /** 요청 목록을 우리 양식 엑셀로 내려받습니다. (전체/선택 공용) */
  // 유상/무상 회사 표준 양식 컬럼 (발주서와 동일)
  const FREE_COLS = ["주문번호","상품명","옵션","수량","주문일","주문시간","수령자이름","수령자연락처","수령자주소","배송메세지","상품코드"];
  const PAID_COLS = ["주문번호","상품명","옵션","수량","주문일","주문시간","수령자이름","수령자연락처","수령자주소","배송메세지","판매가"];

  /**
   * 회사 표준 양식(무상 13컬럼 / 유상 11컬럼)으로 엑셀을 내려받습니다.
   * 각 상품이 한 줄이 되고, 무상·유상은 시트를 나눠서 담습니다.
   */
  function exportRowsToExcel(rows, filePrefix) {
    if (!rows.length) { alert("내려받을 항목이 없어요."); return; }
    const timePart = (ts) => (ts && ts.length >= 16) ? ts.slice(11, 16) : "";
    const datePart = (r) => r.requestDate || (r.receivedAt || "").slice(0, 10);
    const splitZip = (addr) => {
      const m = /^\s*\(?(\d{5})\)?\s*(.*)$/.exec(addr || "");
      return m ? { zip: m[1], rest: m[2].trim() } : { zip: "", rest: (addr || "") };
    };
    // 주문번호 = 다운로드 날짜(YYMMDD)_수령자이름 — 이지어드민에 올릴 때 건마다 구분되도록.
    const stampYYMMDD = new Date().toISOString().slice(2, 10).replace(/-/g, "");
    const orderNo = (r) => stampYYMMDD + "_" + (r.receiver.name || r.requestId);
    const freeRows = [], paidRows = [];
    rows.forEach(r => {
      const prods = (r.products && r.products.length) ? r.products : [{ name: "", option: "", qty: "" }];
      const z = splitZip(r.receiver.address);
      prods.forEach(p => {
        if (r.billing === "paid") {
          paidRows.push({
            "주문번호": orderNo(r), "상품명": p.name || "", "옵션": p.option || "", "수량": p.qty ?? "",
            "주문일": datePart(r), "주문시간": timePart(r.receivedAt),
            "수령자이름": r.receiver.name || "", "수령자연락처": r.receiver.phone || "",
            "수령자주소": r.receiver.address || "", "배송메세지": r.receiver.message || "", "판매가": ""
          });
        } else {
          freeRows.push({
            "주문번호": orderNo(r), "상품명": p.name || "", "옵션": p.option || "", "수량": p.qty ?? "",
            "주문일": datePart(r), "주문시간": timePart(r.receivedAt),
            "수령자이름": r.receiver.name || "", "수령자연락처": r.receiver.phone || "",
            "수령자주소": r.receiver.address || "", "배송메세지": r.receiver.message || "",
            "상품코드": p.code || ""
          });
        }
      });
    });
    if (!freeRows.length && !paidRows.length) { alert("내려받을 상품이 없어요."); return; }
    const wb = XLSX.utils.book_new();
    if (freeRows.length) XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(freeRows, { header: FREE_COLS }), "무상");
    if (paidRows.length) XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(paidRows, { header: PAID_COLS }), "유상");
    const stamp = new Date().toISOString().slice(0, 10);
    XLSX.writeFile(wb, `${filePrefix}_${stamp}.xlsx`);
  }

  /** 지금 화면에 보이는(필터 적용된) 목록 전체를 엑셀로 내려받습니다. */
  document.getElementById("exportExcelBtn").addEventListener("click", () => {
    exportRowsToExcel(getFilteredRows(), "출고요청_목록");
  });

  /** 왼쪽에서 체크한 주문건만 엑셀로 내려받습니다. */
  document.getElementById("exportSelectedBtn").addEventListener("click", () => {
    if (!selected.size) { alert("먼저 왼쪽 체크박스로 주문건을 선택해 주세요."); return; }
    const rows = DATA.filter(r => selected.has(r.requestId));
    exportRowsToExcel(rows, "출고요청_선택");
  });

  /** '선택 진행'(안전재고 전용) — 체크한 요청들을 다음 단계로 진행합니다. (대기→진행중→완료) */
  document.getElementById("bulkAdvanceBtn").addEventListener("click", async () => {
    const ids = [...selected];
    if (!ids.length) { alert("먼저 왼쪽 체크박스로 진행할 요청을 골라 주세요."); return; }
    const advBtn = document.getElementById("bulkAdvanceBtn");
    advBtn.disabled = true;
    let ok = 0; const failed = [];
    for (const id of ids) {
      const r = DATA.find(x => x.requestId === id);
      if (!r) { failed.push(id); continue; }
      const nextKo = SAFETY_NEXT[r.status] || "진행중";
      try {
        const res = await fetch(API_CREATE + "/" + encodeURIComponent(id) + "/status", {
          method: "PATCH", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ status: nextKo })
        });
        if (!res.ok) throw new Error("서버 " + res.status);
        ok += 1;
      } catch (e) { failed.push(id); }
    }
    selected.clear();
    await fetchFromServer();
    advBtn.disabled = false;
    alert(failed.length ? `${ok}건 진행 완료. ${failed.length}건 실패: ${failed.join(", ")}` : `${ok}건을 다음 단계로 진행했어요.`);
  });

  /** '선택 수정' — 체크한 요청 1건을 골라 그 요청 수정 화면으로 엽니다. (수정은 한 번에 한 건) */
  document.getElementById("bulkEditBtn").addEventListener("click", () => {
    const ids = [...selected];
    if (ids.length === 0) { alert("먼저 왼쪽 체크박스로 수정할 요청 1건을 골라 주세요."); return; }
    if (ids.length > 1) { alert("수정은 한 번에 한 건만 할 수 있어요. 요청 1건만 선택해 주세요."); return; }
    const r = DATA.find(x => x.requestId === ids[0]);
    if (!r) { alert("선택한 요청을 찾을 수 없어요. 새로 고침 후 다시 시도해 주세요."); return; }
    if (r.status === "cancelled") { alert("취소된 요청은 수정할 수 없어요."); return; }
    selected.clear(); updateSelectionUI();
    prefillFormForEdit(r);
  });

  /** '선택 삭제' — 체크한 요청들을 한 번에 삭제(취소) 처리합니다.
   *  서버에는 '취소' 상태로 남아 기록은 보존되고, 목록에서는 빠져요. (개별 '취소'와 동일 처리) */
  document.getElementById("bulkDeleteBtn").addEventListener("click", async () => {
    const ids = [...selected];
    if (!ids.length) { alert("먼저 왼쪽 체크박스로 삭제할 요청을 골라 주세요."); return; }

    // 실수 삭제 방지: 개별 취소와 똑같이 '변경자·사유'를 건마다 입력받습니다.
    // 다중이면 한 건씩 물어보고, 중간에 '닫기'를 누르면 아무것도 삭제하지 않고 전부 취소합니다.
    // (여러 건일 때는 "이 내용으로 남은 N건 모두 일괄 적용" 버튼으로 한 번에 끝낼 수도 있어요.)
    const plans = [];
    for (let n = 0; n < ids.length; n++) {
      const id = ids[n];
      const r = DATA.find(x => x.requestId === id);
      const label = id + (r ? ` (${productSummary(r)})` : "") + (ids.length > 1 ? ` — ${n + 1}/${ids.length}건째` : "") + " 삭제";
      const info = await askChange(label, { bulkRemaining: ids.length - n });
      if (!info) {   // 사용자가 '닫기' → 지금까지 아무것도 삭제하지 않고 전체 중단
        showBanner("삭제를 취소했어요. 선택한 요청은 그대로 남아 있어요.", "error");
        setTimeout(() => showBanner(""), 3000);
        return;
      }
      if (info.bulkAll) {   // 남은 건 전체(현재 건 포함)에 동일하게 적용하고 입력 종료
        for (let m = n; m < ids.length; m++) plans.push({ id: ids[m], actor: info.actor, reason: info.reason });
        break;
      }
      plans.push({ id, actor: info.actor, reason: info.reason });
    }

    const delBtn = document.getElementById("bulkDeleteBtn");
    delBtn.disabled = true;
    const original = delBtn.innerHTML;
    let ok = 0; const failed = [];
    for (const p of plans) {
      delBtn.innerHTML = `삭제 중... (${ok + failed.length + 1}/${plans.length})`;
      try {
        const qs = "?changedBy=" + encodeURIComponent(p.actor) + "&changeReason=" + encodeURIComponent(p.reason);
        const res = await fetch(API_CREATE + "/" + encodeURIComponent(p.id) + "/cancel" + qs, { method: "PATCH" });
        if (!res.ok) throw new Error("서버 응답 " + res.status);
        ok += 1;
      } catch (e) { failed.push(p.id); }
    }
    selected.clear();
    delBtn.disabled = false;
    delBtn.innerHTML = original;
    await fetchFromServer();
    if (failed.length) {
      alert(`${ok}건 삭제 완료.\n${failed.length}건은 실패했어요: ${failed.join(", ")}\n잠시 후 다시 시도해 주세요.`);
    } else {
      alert(`${ok}건을 모두 삭제했어요. (사유는 각 요청의 변경 이력에 남았어요)`);
    }
  });

  /** 헤더 '전체 선택' — 지금 목록에 보이는 주문건을 한 번에 선택/해제합니다. */
  document.getElementById("selectAll").addEventListener("change", (e) => {
    const on = e.target.checked;
    document.querySelectorAll("#tableBody tr.mainrow").forEach(tr => {
      const id = tr.dataset.id;
      if (on) selected.add(id); else selected.delete(id);
      const cb = tr.querySelector(".rowSelect");
      if (cb) cb.checked = on;
    });
    updateSelectionUI();
  });
  document.querySelectorAll(".tab").forEach(t => t.addEventListener("click", () => {
    document.querySelectorAll(".tab").forEach(x => x.classList.remove("active"));
    t.classList.add("active");
    activeStatus = t.dataset.status;
    clearTodayFilter();
    renderTable();
  }));
  // 보기 전환 버튼 (목록 / 캘린더 / 칸반)
  document.querySelectorAll("#viewSwitch .vbtn").forEach(b => b.addEventListener("click", () => {
    viewMode = b.dataset.vm;
    updateViewModeButtons();
    renderTable();
  }));
  document.getElementById("searchInput").addEventListener("input", (e) => {
    searchTerm = e.target.value;
    if (activeView === "form") setView("all");
    clearTodayFilter();
    renderTable();
  });
  document.getElementById("teamFilter").addEventListener("change", (e) => {
    activeTeam = e.target.value || null;
    clearTodayFilter();
    renderTable();
  });
  document.getElementById("assigneeFilter").addEventListener("change", (e) => {
    activeAssignee = e.target.value || null;
    clearTodayFilter();
    renderTable();
  });
  document.getElementById("channelFilter").addEventListener("change", (e) => {
    activeChannel = e.target.value || null;
    clearTodayFilter();
    renderTable();
  });
  document.getElementById("clearTodayFilter").addEventListener("click", () => setView(activeView));

  // 상세 검색 — 검색어(수기 입력)·요청자·기간. 전체 요청·안전재고 화면에 공통으로 적용됩니다.
  function applySearchPanel() {
    searchTerm = document.getElementById("spKeyword").value.trim();
    document.getElementById("searchInput").value = searchTerm;   // 위쪽 검색창과 값을 맞춰서 헷갈리지 않게
    activeRequester = document.getElementById("spRequester").value || null;
    dateRangeField = document.getElementById("spDateField").value;
    dateRangeFrom = document.getElementById("spDateFrom").value;
    dateRangeTo = document.getElementById("spDateTo").value;
    if (activeView === "form") setView("all");
    clearTodayFilter();
    renderTable();
  }
  document.getElementById("spApply").addEventListener("click", applySearchPanel);
  document.getElementById("spKeyword").addEventListener("keydown", (e) => {
    if (e.key === "Enter") { e.preventDefault(); applySearchPanel(); }
  });
  document.getElementById("spClear").addEventListener("click", () => {
    document.getElementById("spKeyword").value = "";
    document.getElementById("searchInput").value = "";
    document.getElementById("spRequester").value = "";
    document.getElementById("spDateFrom").value = "";
    document.getElementById("spDateTo").value = "";
    searchTerm = "";
    activeRequester = null;
    dateRangeFrom = ""; dateRangeTo = "";
    clearTodayFilter();
    renderTable();
  });
  document.getElementById("bellBtn").addEventListener("click", () => {
    activeStatus = "";
    activeTeam = null;
    activeAssignee = null;
    activeChannel = null;
    searchTerm = "";
    document.querySelectorAll(".tab").forEach(x => x.classList.remove("active"));
    document.querySelector('.tab[data-status=""]').classList.add("active");
    document.getElementById("assigneeFilter").value = "";
    document.getElementById("channelFilter").value = "";
    document.getElementById("searchInput").value = "";
    setView("all");
    renderTeamChips();
    renderTable();
    document.querySelector(".tablecard").scrollIntoView({ behavior: "smooth", block: "start" });
  });
  document.getElementById("todaySeeAll").addEventListener("click", () => {
    todayOnly = true;
    activeStatus = "";
    activeTeam = null;
    activeAssignee = null;
    activeChannel = null;
    activeRequester = null;
    dateRangeFrom = ""; dateRangeTo = "";
    searchTerm = "";
    document.querySelectorAll(".navitem").forEach(b => b.classList.remove("active"));
    document.querySelectorAll(".tab").forEach(x => x.classList.remove("active"));
    document.querySelector('.tab[data-status=""]').classList.add("active");
    document.getElementById("assigneeFilter").value = "";
    document.getElementById("channelFilter").value = "";
    document.getElementById("searchInput").value = "";
    const spReq = document.getElementById("spRequester"); if (spReq) spReq.value = "";
    const spFrom = document.getElementById("spDateFrom"); if (spFrom) spFrom.value = "";
    const spTo = document.getElementById("spDateTo"); if (spTo) spTo.value = "";
    document.getElementById("formSection").style.display = "none";
    document.getElementById("logisticsSection").style.display = "none";
    document.querySelector(".tablecard").style.display = "";
    document.getElementById("searchPanel").style.display = "";
    renderTeamChips();
    setFocusMode(true);
    document.getElementById("pageTitle").textContent = "오늘의 업무";
    document.getElementById("pageSub").textContent = "출고 희망일이 오늘인 업무만 모아서 보여줘요";
    document.getElementById("tableTitle").textContent = "오늘의 업무 상세";
    document.getElementById("clearTodayFilter").style.display = "";
    renderTable();
    window.scrollTo({ top: 0, behavior: "smooth" });
  });
  // ===== 어떤 양식이든 읽어내는 공통 파서 (2026-08-27) =====
  // 타팀 발주서는 '표 한 장'이 아니라 '양식 문서'인 경우가 많습니다.
  //   · 제목줄이 1행이 아니라 한참 아래(실제 사례: 18행)에 있고
  //   · 수령정보가 표 컬럼이 아니라 표 위 '입 고 처 / 담 당 자' 같은 항목-값 칸에 적혀 있고
  //   · '공급자(우리 회사)'와 '발주처(받는 곳)'가 나란히 적혀 있습니다.
  // 그래서 ① 제목줄을 점수로 찾고 ② 표 밖 항목-값 칸까지 훑고
  // ③ 공급자 구역 값은 절대 쓰지 않도록 구역을 나눠 읽습니다.
  // ③이 특히 중요합니다 — 잘못 집으면 우리 사무실로 배송됩니다.

  const META_LABELS = [
    { names: ["입고처","배송지","납품처","수령처","도착지","인도장소","납품장소","배송처","인도처"], key: "place",  score: 100 },
    { names: ["수령인","수령자","수취인","받는분","받는사람","인수자","수하인","수령자명"],           key: "name",   score: 90  },
    { names: ["담당자","담당","연락담당","담당자명","담당자연락처"],                                  key: "person", score: 60  },
    { names: ["상호","업체명","거래처명","회사명","매장명","지점명","매장","지점","점포명"],           key: "store",  score: 50  },
    { names: ["소재지","주소","사업장주소","사업장소재지","본사주소"],                                key: "addr",   score: 40  },
    { names: ["연락처","전화","전화번호","휴대폰","핸드폰","연락번호","대표번호"],                     key: "phone",  score: 40  },
    { names: ["배송메세지","배송메시지","요청사항","전달사항","배송요청","특이사항","비고"],           key: "msg",    score: 30  },
  ];
  // 구역 표시 — 이 단어가 보이면 그 오른쪽·아래는 해당 구역으로 봅니다.
  const BUYER_MARKS    = ["발주처","buyer","수령처","입고처","납품처","주문처","고객사","받는곳","수요처"];
  const SUPPLIER_MARKS = ["공급자","supplier","공급처","제조사","판매자","발송처","출고처","보내는곳","수주처"];

  const RE_PHONE  = /(0\d{1,2})[-\s.]?(\d{3,4})[-\s.]?(\d{4})/;
  const RE_REGION = /(특별시|광역시|특별자치시|특별자치도|서울|부산|대구|인천|광주|대전|울산|세종|경기|강원|충북|충남|전북|전남|경북|경남|제주|[가-힣]{2}도)/;
  const RE_KRNAME = /^[가-힣]{2,4}$/;

  /** 엑셀 첫 시트를 2차원 문자열 배열로 읽습니다. (병합셀은 좌상단에만 값이 들어옵니다) */
  async function readGrid(file) {
    const buf = await file.arrayBuffer();
    const wb = XLSX.read(buf, { type: "array" });
    const ws = wb.Sheets[wb.SheetNames[0]];
    // range: 0 을 줘야 시트 "첫 줄"부터 읽습니다.
    // (빈 줄로 시작하는 양식은 기본값이 그 줄을 건너뛰어, 제목줄 18행을 17행이라고 잘못 안내합니다)
    const rows = XLSX.utils.sheet_to_json(ws, { header: 1, defval: "", blankrows: true, range: 0 });
    return (rows || []).map(r => (r || []).map(v => String(v == null ? "" : v).trim()));
  }

  /** 별칭 사전을 '정규화된 이름 → 우리 필드' 형태로 펼칩니다. */
  function flatAliasMap() {
    const flat = {};
    for (const k in IMPORT_ALIASES) for (const a of IMPORT_ALIASES[k]) flat[impNorm(a)] = k;
    return flat;
  }

  // 제목줄 판정에 쓰는 '표 컬럼다운' 필드들
  const TABLE_KEYS = { pname:1, popt:1, qty:1, qtyOrd:1, qtyOut:1, code:1, price:1, orderNo:1, name:1, addr:1, phone:1, store:1 };

  /** 제목줄이 몇 번째 줄인지 점수로 찾습니다. (1행 고정 가정 금지 — 18행인 파일이 실제로 있습니다) */
  function findHeaderRow(grid, flat, scan) {
    let bestI = -1, bestScore = 0;
    const to = Math.min(grid.length, scan || 40);
    for (let i = 0; i < to; i++) {
      const seen = new Set();
      (grid[i] || []).forEach(c => {
        const k = flat[impNorm(c)];
        if (k && TABLE_KEYS[k]) seen.add((k === "qtyOrd" || k === "qtyOut") ? "qty" : k);
      });
      if (seen.size > bestScore) { bestScore = seen.size; bestI = i; }
    }
    return { idx: bestI, score: bestScore };
  }

  /** 표 위 구역(공급자/발주처) 표시를 모아, 특정 칸이 어느 구역인지 알려주는 함수를 돌려줍니다. */
  function sectionResolver(grid, upto) {
    const marks = [];
    for (let r = 0; r < upto; r++) {
      const row = grid[r] || [];
      for (let c = 0; c < row.length; c++) {
        const n = impNorm(row[c]);
        if (!n) continue;
        if (BUYER_MARKS.some(m => n.includes(m)))         marks.push({ r, c, kind: "buyer" });
        else if (SUPPLIER_MARKS.some(m => n.includes(m))) marks.push({ r, c, kind: "supplier" });
      }
    }
    // 어떤 칸의 구역 = 그 칸보다 위/왼쪽에 있는 표시 중 가장 가까운(=가장 나중에 나온) 것
    return function kindAt(r, c) {
      let best = null, bestKey = null;
      for (const m of marks) {
        if (m.r <= r && m.c <= c) {
          const key = m.r * 1000 + m.c;
          if (bestKey === null || key > bestKey) { bestKey = key; best = m.kind; }
        }
      }
      return best;
    };
  }

  /** 라벨 칸 오른쪽(같은 줄)에서 첫 값. 없으면 바로 아래 칸. */
  function valueBeside(grid, r, c) {
    const row = grid[r] || [];
    for (let cc = c + 1; cc < Math.min(row.length, c + 9); cc++) if (row[cc]) return row[cc];
    const below = grid[r + 1] || [];
    if (below[c]) return below[c];
    return "";
  }

  /** 한 칸에 여러 정보가 뭉쳐 있는 경우를 쪼갭니다.
   *  실제 예: "서울시 서초구 ... 웰니스하우스서울  /  수령인 : 이수정" → 주소 + 이름 */
  function splitComposite(text) {
    const out = { addr: "", name: "", phone: "", store: "" };
    let t = String(text || "");
    if (!t) return out;
    const pm = t.match(RE_PHONE);
    if (pm) { out.phone = pm[1] + "-" + pm[2] + "-" + pm[3]; t = t.slice(0, pm.index) + " " + t.slice(pm.index + pm[0].length); }
    const parts = t.split(/[/|·]|\s{3,}/).map(x => x.trim()).filter(Boolean);
    for (let p of parts) {
      const lm = p.match(/^\s*([가-힣A-Za-z ]{2,10})\s*[:：]\s*(.+)$/);
      if (lm) {
        const lab = impNorm(lm[1]), val = lm[2].trim();
        let key = null;
        for (const L of META_LABELS) if (L.names.some(n => impNorm(n) === lab)) { key = L.key; break; }
        if (key === "name" || key === "person") { out.name  = out.name  || val; continue; }
        if (key === "addr" || key === "place")  { out.addr  = out.addr  || val; continue; }
        if (key === "phone")                    { out.phone = out.phone || val; continue; }
        if (key === "store")                    { out.store = out.store || val; continue; }
        p = val;
      }
      if (RE_REGION.test(p) && p.length > 10)      out.addr  = out.addr  || p;
      else if (!out.name && RE_KRNAME.test(p))     out.name  = p;
      else if (!out.store && p.length <= 30)       out.store = p;
    }
    return out;
  }

  /** 표 '밖'의 항목-값 칸에서 수령정보를 찾아냅니다. 공급자 구역 값은 쓰지 않습니다. */
  function extractReceiverMeta(grid, headerIdx) {
    const upto = headerIdx > 0 ? headerIdx : grid.length;
    const kindAt = sectionResolver(grid, upto);
    const cand = {};
    const put = (f, v, sc) => { if (v && (!cand[f] || sc > cand[f].sc)) cand[f] = { sc, v: String(v).trim() }; };

    for (let r = 0; r < upto; r++) {
      const row = grid[r] || [];
      for (let c = 0; c < row.length; c++) {
        const n = impNorm(row[c]);
        if (!n || n.length > 12) continue;
        const L = META_LABELS.find(x => x.names.some(y => impNorm(y) === n));
        if (!L) continue;
        const raw = valueBeside(grid, r, c);
        if (!raw) continue;
        const sect = kindAt(r, c);
        // 공급자(보내는 쪽 = 우리 회사) 구역은 '입고처'만 예외로 인정합니다.
        if (sect === "supplier" && L.key !== "place") continue;
        const sc = L.score + (sect === "buyer" ? 20 : 0);
        const parts = splitComposite(raw);
        if (L.key === "place") {
          put("addr", parts.addr || raw, sc);
          put("name", parts.name, sc);
          put("phone", parts.phone, sc);
          put("store", parts.store, sc - 5);
        } else if (L.key === "name" || L.key === "person") {
          put("name", parts.name || raw.replace(RE_PHONE, "").trim(), sc);
          put("phone", parts.phone, sc);
        } else if (L.key === "addr")  { put("addr", parts.addr || raw, sc); }
        else if (L.key === "phone")   { put("phone", parts.phone || raw, sc); }
        else if (L.key === "store")   { put("store", parts.store || raw, sc); }
        else if (L.key === "msg")     { put("msg", raw, sc); }
      }
    }
    const out = {};
    for (const k in cand) out[k] = cand[k].v;
    // 상호에 붙은 "(대표 : 홍길동)" 같은 꼬리표는 떼어냅니다.
    if (out.store) out.store = out.store.replace(/[（(][^)）]*[)）]\s*$/, "").trim();
    return out;
  }

  /** 제목줄 아래 표에서 상품 줄만 뽑습니다. */
  function readProductsFrom(grid, headerIdx, flat) {
    const header = grid[headerIdx] || [];
    const col = {};
    const codeCols = [];
    header.forEach((c, i) => {
      const k = flat[impNorm(c)];
      if (!k) return;
      if (col[k] === undefined) col[k] = i;
      if (k === "code") codeCols.push(i);
    });
    const g = (row, k) => (col[k] === undefined ? "" : String(row[col[k]] == null ? "" : row[col[k]]).trim());
    const products = [];
    for (let r = headerIdx + 1; r < grid.length; r++) {
      const row = grid[r] || [];
      if (!row.some(v => String(v || "").trim())) continue;
      const nm = g(row, "pname");
      const codes = [...new Set(codeCols.map(i => String(row[i] == null ? "" : row[i]).trim()).filter(Boolean))];
      const cd = codes[0] || "";
      if (!nm && !cd) continue;
      if (/소계|합계|총계|subtotal|total/i.test(nm + " " + cd)) continue;
      let qty = 0;
      for (const k of ["qtyOut", "qtyOrd", "qty"]) {
        const v = parseInt(g(row, k).replace(/[^\d]/g, ""), 10);
        if (Number.isFinite(v) && v > 0) { qty = v; break; }
      }
      products.push({ code: cd, codes, name: nm || cd, rawName: nm || cd, option: g(row, "popt") || "없음", qty: qty || 1 });
    }
    return { col, products };
  }

  /** 표 컬럼 기준으로 '배송지가 몇 곳인지' 셉니다. (0 = 표에 수령자 칸이 아예 없음) */
  function countTableDestinations(grid, headerIdx, flat) {
    const header = grid[headerIdx] || [];
    const col = {};
    header.forEach((c, i) => { const k = flat[impNorm(c)]; if (k && col[k] === undefined) col[k] = i; });
    const keys = ["name", "addr", "store"].filter(k => col[k] !== undefined);
    if (!keys.length) return 0;
    const seen = new Set();
    let carry = {};
    for (let r = headerIdx + 1; r < grid.length; r++) {
      const row = grid[r] || [];
      if (!row.some(v => String(v || "").trim())) continue;
      keys.forEach(k => { const v = String(row[col[k]] == null ? "" : row[col[k]]).trim(); if (v) carry[k] = v; });
      const sig = keys.map(k => impNorm(carry[k] || "")).join("|");
      if (sig.replace(/\|/g, "")) seen.add(sig);
    }
    return seen.size;
  }

  // ===== 발주서(.xls/.xlsx) 자동 채우기 =====

  /** 발주서 하나를 읽어서 {receiver, products, dests, ...} 로 돌려줍니다 (파일 저장은 안 함). */
  async function parseOrderFile(file) {
    const grid = await readGrid(file);
    if (!grid.length) throw new Error(file.name + ": 빈 파일이에요.");
    const flat = flatAliasMap();

    const h = findHeaderRow(grid, flat);
    if (h.idx < 0 || h.score < 2) {
      throw new Error(file.name + ": 상품 표의 제목줄(상품명·수량·코드 등)을 찾지 못했어요.");
    }
    const { col, products } = readProductsFrom(grid, h.idx, flat);
    if (!products.length) throw new Error(file.name + ": 표는 찾았지만 상품 줄이 없어요.");

    // ① 표 밖 항목-값 칸에서 수령정보 (이 파일 형식의 핵심)
    const meta = extractReceiverMeta(grid, h.idx);
    // ② 표 안에 수령자 컬럼이 있으면 그게 더 정확하므로 우선 사용
    const fromTable = {};
    ["name", "phone", "addr", "msg", "store", "zip"].forEach(k => {
      if (col[k] === undefined && k !== "zip") return;
      for (let r = h.idx + 1; r < grid.length; r++) {
        const idx = (k === "zip") ? col.zip : col[k];
        if (idx === undefined) break;
        const v = String((grid[r] || [])[idx] || "").trim();
        if (v) { fromTable[k] = v; break; }
      }
    });
    const phoneRaw = fromTable.phone || meta.phone || "";
    const pm = String(phoneRaw).match(RE_PHONE);
    const zip = fromTable.zip || "";
    let addr = fromTable.addr || meta.addr || "";
    if (zip && addr && !addr.startsWith("(")) addr = "(" + zip + ") " + addr;

    // 수령자·주소 칸이 따로 없고 '매장' 칸만 있는 발주서(예: 이지어드민 수주내역일괄출고 양식)는
    // 다중등록과 똑같이 우리 매장 주소록(OFFBEAUTY_STORES)에서 주소·연락처를 채웁니다.
    // 이게 없으면 "매장"만 적힌 파일은 전부 "받는 사람 정보를 못 찾았다"며 거부돼 버립니다.
    const storeRaw = fromTable.store || meta.store || "";
    const storeHit = storeRaw ? matchStore(storeRaw) : null;
    if (!addr && storeHit) addr = storeHit.addr || "";

    const receiver = {
      name:    fromTable.name || meta.name || (storeHit && storeHit.name) || storeRaw || "",
      phone:   pm ? (pm[1] + "-" + pm[2] + "-" + pm[3]) : (String(phoneRaw).trim() || (storeHit && storeHit.phone) || ""),
      address: addr,
      message: fromTable.msg || meta.msg || "",
      store:   storeRaw,
    };

    // 이름·주소를 둘 다 못 찾았으면 배송 양식이 아니라 상품 목록 같은 참고용 파일입니다.
    // (표는 있어도 '누구에게 보낼지'가 없으면 발주서로 취급하지 않습니다)
    if (!receiver.name && !receiver.address) {
      throw new Error(file.name + ": 받는 사람 정보(수령자·주소)를 찾지 못했어요. 배송 양식이 아닌 것 같아요.");
    }

    return {
      fileName: file.name, receiver, products,
      headerRow: h.idx + 1,
      dests: countTableDestinations(grid, h.idx, flat),
      foundBy: (fromTable.name || fromTable.addr) ? "표 컬럼" : "양식 칸",
    };
  }

  // 잘못된 칸에 올라간 파일을 반대쪽 칸으로 옮겨 주기 위한 도우미
  // (저장소가 있는 칸이면 그 칸의 기존 선택을 이 파일들로 완전히 바꿉니다 — 옮기는 것이지 더하는 게 아니라서)
  function moveFilesTo(targetInputId, files, run) {
    if (fileStore[targetInputId] !== undefined) {
      setStoreFiles(targetInputId, files.slice());
    } else {
      try {
        const dt = new DataTransfer();
        files.forEach(f => dt.items.add(f));
        const el = document.getElementById(targetInputId);
        if (el) el.files = dt.files;
      } catch (e) { /* 브라우저가 막으면 입력칸 표시만 못 할 뿐, 처리는 그대로 진행 */ }
    }
    run();
  }

  /** 인식 결과를 사람이 읽을 수 있게 요약합니다. 못 찾은 항목은 빨갛게 알려 줍니다. */
  function receiverSummaryHtml(recv, extra) {
    const miss = [];
    if (!recv.name)    miss.push("수령자 이름");
    if (!recv.phone)   miss.push("연락처");
    if (!recv.address) miss.push("주소");
    const line = (lab, v) => v
      ? `<div><span style="color:var(--text-muted);">${lab}</span> <b>${esc(v)}</b></div>`
      : `<div><span style="color:var(--text-muted);">${lab}</span> <b style="color:var(--danger);">못 찾음 — 직접 입력해 주세요</b></div>`;
    return `
      <div style="margin-top:8px;padding:10px 12px;border:1px solid var(--border);border-radius:8px;background:var(--surface-alt,#fafbfc);line-height:1.7;">
        <div style="font-weight:700;margin-bottom:4px;">읽어낸 수령정보 ${extra ? `<span style="font-weight:400;color:var(--text-muted);">(${esc(extra)})</span>` : ""}</div>
        ${line("이름", recv.name)}
        ${line("연락처", recv.phone)}
        ${line("주소", recv.address)}
        ${recv.message ? line("배송메세지", recv.message) : ""}
        ${miss.length ? `<div style="margin-top:6px;color:var(--danger);font-weight:600;">⚠️ ${esc(miss.join(" · "))} 은(는) 위 칸에서 직접 채워 주세요.</div>` : ""}
      </div>`;
  }

  /**
   * 여러 발주서를 한 번에 처리합니다 (합포장 — 지점별 발주서를 한 요청에 묶어서 출고).
   * 상품은 모든 파일에서 합치고, 수령정보는 첫 번째 파일 기준으로 채웁니다.
   * 배송지가 여러 곳인 파일이면 여기서 등록하지 않고 '다중 등록' 칸으로 안내합니다.
   */
  async function handleOrderFiles(fileList) {
    const msgEl = document.getElementById("fOrderFileMsg");
    msgEl.style.color = "";
    msgEl.textContent = "읽는 중...";

    const files = Array.from(fileList);
    // 파일마다 '발주서 양식'으로 읽어 봅니다. 안 읽히면 참고용 첨부로 취급합니다(오류 아님) —
    // 요청자가 발주서 1장 + 안내문·주소록 같은 참고파일을 함께 올리는 경우가 있어서입니다.
    const parsed = [];
    const refOnly = [];
    for (const f of files) {
      try { parsed.push(await parseOrderFile(f)); }
      catch (e) { refOnly.push(f); }
    }

    if (!parsed.length) {
      msgEl.style.color = "var(--danger)";
      msgEl.textContent = files.length > 1
        ? "올린 파일 중 상품·수령정보를 읽을 수 있는 발주서가 없어요. 직접 입력해 주셔도 돼요."
        : "파일을 읽지 못했어요: 형식을 확인해 주세요. 직접 입력해 주셔도 돼요.";
      orderFilesForAttach = files.concat(fileStore.fOrderRefFiles);   // 읽지는 못했어도 첨부는 그대로 유지 + 참고용 서류
      return;
    }

    // ── 칸 분리 ① 배송지가 여러 곳인 파일은 여기가 아니라 '다중 등록' 칸입니다.
    const multi = parsed.find(p => p.dests >= 2);
    if (multi) {
      msgEl.style.color = "var(--danger)";
      msgEl.innerHTML = `이 파일은 배송지가 <b>${multi.dests}곳</b>이에요. 여기(합포장)에 올리면 한 곳으로만 나갑니다.<br>
        위쪽 <b>📥 여러 배송지 한 번에 등록 (다중)</b> 에 올려야 배송지마다 별도 요청서가 만들어져요.
        <button type="button" class="btn small" id="moveToBulkBtn" style="margin-top:8px;">📥 그 칸으로 옮기기</button>`;
      const btn = document.getElementById("moveToBulkBtn");
      if (btn) btn.addEventListener("click", () => {
        // 옮기고 나면 발주서 칸 저장소·표시를 완전히 비웁니다 (안 비우면 다음 파일과 잘못 합쳐집니다)
        fileStore.fOrderFile = [];
        const fi = document.getElementById("fOrderFile"); if (fi) fi.value = "";
        document.getElementById("fOrderFileName").innerHTML = "";
        msgEl.innerHTML = "";
        moveFilesTo("fBulkFile", files, refreshBulkFiles);
      });
      return;
    }

    // ── 합포장은 '여러 발주서 → 같은 한 곳'일 때만 안전합니다.
    //    수령지가 다르면 첫 파일 주소로 조용히 합쳐버려서, 다른 지점 상품이 엉뚱한 곳으로 나갈 수 있습니다.
    //    그래서 경고만 하고 넘어가지 않고, 아예 막습니다.
    const sigs = [...new Set(parsed.map(p => impNorm((p.receiver.name || "") + (p.receiver.address || ""))).filter(Boolean))];
    if (sigs.length > 1) {
      msgEl.style.color = "var(--danger)";
      msgEl.innerHTML = `⚠️ 올린 발주서들의 <b>수령지가 서로 달라요</b>.<br>
        이 칸은 <b>합포장(한 곳으로 여러 상품)</b> 전용이라, 지금처럼 지점마다 주소가 다르면
        한 곳으로 합쳐 등록할 수 없어요 — 다른 지점 상품이 엉뚱한 곳으로 나갈 수 있습니다.<br>
        수령지가 같은 파일들끼리만 함께 올리거나, <b>한 번에 하나씩</b> 따로 올려서 각각 등록해 주세요.`;
      return;
    }

    orderFilesForAttach = files.concat(fileStore.fOrderRefFiles);   // 발주서 + ② 참고용 서류 전부 요청서에 함께 첨부되도록 보관

    const first = parsed[0].receiver;
    const setVal = (id, v) => { if (v) document.getElementById(id).value = v; };
    setVal("fRcvName", first.name);
    setVal("fRcvPhone", first.phone);
    setVal("fRcvAddr", first.address);
    setVal("fRcvMsg", first.message);

    document.getElementById("productRows").innerHTML = "";
    let added = 0;
    parsed.forEach(p => {
      p.products.forEach(prod => {
        addProductRow(false);
        const row = document.querySelector("#productRows .prow:last-child");
        row.querySelector(".pName").value = prod.name;
        row.querySelector(".pOpt").value = prod.option;
        row.querySelector(".pQty").value = prod.qty;
        added += 1;
      });
    });
    if (!added) addProductRow(false);

    const fileWord = parsed.length > 1 ? `발주서 ${parsed.length}건 (합포장)` : "발주서 1건";
    const refWord = refOnly.length ? ` · 참고용 파일 ${refOnly.length}건은 요청서에 그대로 첨부돼요` : "";
    const where = `${parsed[0].headerRow}행에서 표를 찾음 · 수령정보는 ${parsed[0].foundBy}에서 읽음`;
    msgEl.style.color = "var(--accent)";
    msgEl.innerHTML = `${esc(fileWord)} · 상품 <b>${added}건</b>을 채웠어요.${esc(refWord)}`
      + receiverSummaryHtml(first, where);
  }

  /** fOrderFile 저장소가 바뀔 때마다: 칩을 다시 그리고, 쌓인 파일 전체로 다시 채웁니다. */
  function refreshOrderFile() {
    const store = fileStore.fOrderFile;
    renderFileChips(document.getElementById("fOrderFileName"), store, (i) => {
      removeFileFromStore("fOrderFile", i);
      refreshOrderFile();
    });
    if (store.length) {
      handleOrderFiles(store);
    } else {
      // 마지막 발주서를 뺐을 때 — 이미 채워진 상품·수령정보는 그대로 두고 안내문만 정리합니다.
      // (② 참고용 서류는 발주서가 없어도 여전히 함께 첨부돼야 하므로 남겨 둡니다)
      orderFilesForAttach = fileStore.fOrderRefFiles.slice();
      document.getElementById("fOrderFileMsg").innerHTML = "";
    }
  }
  document.getElementById("fOrderFile").addEventListener("change", (e) => {
    // 브라우저가 방금 고른/끌어다 놓은 파일만 e.target.files 에 담아 주므로, 기존 목록에 더합니다.
    addFilesToStore("fOrderFile", e.target.files);
    refreshOrderFile();
  });

  // ===== 여러 배송지 한 번에 등록 (다중 업로드) =====
  // 무상/유상 양식 컬럼. 요청자가 이 양식을 받아 채워서 올리면 각 줄이 별도 요청서가 됩니다.
  // 무상은 물류 다운로드 양식과 동일한 11칸. (주문번호·주문일·주문시간은 비워 두면 자동으로 채워져요)
  const BULK_FREE_COLS = ["주문번호","상품명","옵션","수량","주문일","주문시간","수령자이름","수령자연락처","수령자주소","배송메세지","상품코드"];
  const BULK_PAID_COLS = ["주문번호","상품명","옵션","수량","주문일","주문시간","수령자이름","수령자연락처","수령자주소","배송메세지","상품코드","판매가"];

  /** 무상/유상 다중배송 양식(.xlsx)을 만들어 내려받습니다. (헤더 + 예시 1줄) */
  function downloadBulkTemplate(kind) {
    const paid = (kind === "paid");
    const cols = paid ? BULK_PAID_COLS : BULK_FREE_COLS;
    const example = paid
      ? ["","베이직 지압볼","색상 골드",1,"","","김수령","010-1234-5678","서울 강남구 테헤란로 1, 3층","부재 시 문 앞","W25XX034","19000"]
      : ["","베이직 지압볼","색상 골드",1,"","","김수령","010-1234-5678","서울 강남구 테헤란로 1, 3층","부재 시 문 앞","W25XX034"];
    try {
      const ws = XLSX.utils.aoa_to_sheet([cols, example]);
      const wb = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(wb, ws, "출고요청");
      XLSX.writeFile(wb, (paid ? "유상" : "무상") + "_다중배송_양식.xlsx");
    } catch (e) {
      alert("양식을 만들지 못했어요: " + (e.message || "다시 시도해 주세요."));
    }
  }

  /** 채운 양식 파일을 읽어, 수령자별로 묶어서 여러 개의 요청서를 한 번에 등록합니다. */
  // ---- 상품코드 → 이지어드민 카탈로그(내부코드·옵션·정식상품명) 자동 매칭 ----
  // 타팀 파일의 상품코드는 대부분 이지어드민 "바코드"(88…)라서, 재고조회용 내부코드(S코드)가 아니에요.
  // 그래서 재고검색 API로 그 코드를 조회해 내부코드·옵션·정식 상품명을 끌어와 요청서에 채웁니다.
  // (재고검색은 상품코드/상품명/옵션/바코드를 모두 뒤지므로 바코드로도 정확히 찾힙니다.)
  const _catalogCache = {};
  async function _searchCatalog(q) {
    const key = String(q == null ? "" : q).trim();
    if (!key) return [];
    try {
      const res = await fetch(API_STOCK + "/search?q=" + encodeURIComponent(key) + "&withStock=false");
      if (res.ok) { const list = await res.json(); if (Array.isArray(list)) return list; }
    } catch (e) { /* 네트워크 오류 시 빈 결과 */ }
    return [];
  }

  // ===== 학습(기억) — 관리자가 수동으로 바로잡은 매칭을 서버에 저장해두고, 다음부터 자동 적용 =====
  let MATCH_MEMORY = {};   // 정규화키 -> {productId, option, name}
  /**
   * 옵션이 있으면 이름+옵션을 합쳐서 키를 만듭니다.
   * ⚠️ 이유 — "비브아 손 마사지기"처럼 상품명은 같은데 옵션 칸에 '어댑터'·'2SET' 같이
   * 완전히 다른 구성이 적히는 경우가 있습니다. 옵션 없이 이름만으로 키를 만들면, 누군가
   * 기본형 한 건을 정정한 학습이 이후 어댑터·2SET 같은 다른 구성에도 그대로 적용돼
   * 엉뚱한 상품이 나가는 사고로 이어집니다(실제로 있었던 사고).
   */
  function memKey(s, option) {
    const norm = (x) => String(x == null ? "" : x).replace(/\s+/g, "").replace(/[\[\]()（）·.,\-_/#*★⭐=:：]/g, "").toLowerCase();
    const base = norm(s);
    const opt = (option && option !== "없음") ? norm(option) : "";
    return opt ? base + "§" + opt : base;
  }
  async function loadMatchMemory() {
    try {
      const res = await fetch(API_BASE + "/api/match-memory");
      if (!res.ok) return;
      const list = await res.json();
      const m = {};
      (list || []).forEach(x => { if (x && x.aliasKey) m[x.aliasKey] = { productId: x.productId, option: x.option || "", name: x.name || "" }; });
      MATCH_MEMORY = m;
    } catch (e) { /* 서버/테이블 없으면 학습 없이도 동작 */ }
  }
  async function saveMatchMemory(p, chosen) {
    const keys = new Set();
    // 옵션이 있으면 '이름+옵션' 키로만 저장합니다 (다른 옵션까지 덮어쓰지 않도록).
    // 옵션이 아예 없는 상품만 이름 하나로 저장합니다.
    if (p.rawName) keys.add(memKey(p.rawName, p.option));
    (p.codes || []).forEach(c => { const k = memKey(c); if (k) keys.add(k); });
    for (const k of keys) {
      if (!k) continue;
      MATCH_MEMORY[k] = { productId: chosen.productId, option: chosen.option || "", name: chosen.name || "" };
      try {
        await fetch(API_BASE + "/api/match-memory", {
          method: "POST", headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ aliasKey: k, productId: chosen.productId, option: chosen.option || "", name: chosen.name || "", sourceName: p.rawName || "", updatedBy: "" }),
        });
      } catch (e) { /* 저장 실패해도 이번 세션 매칭은 유지 */ }
    }
  }

  // (1) 코드 하나로 조회 — 내부코드 정확일치 우선, 아니면(바코드 등) 후보가 하나뿐일 때만 채택
  async function resolveCatalogCode(code) {
    const key = String(code == null ? "" : code).trim();
    if (!key) return null;
    if (key in _catalogCache) return _catalogCache[key];
    const list = await _searchCatalog(key);
    const hit = list.find(c => String(c.productId == null ? "" : c.productId).trim() === key)
             || (list.length === 1 ? list[0] : null);
    const val = hit ? { productId: hit.productId, name: hit.name, option: hit.option || "" } : null;
    _catalogCache[key] = val;
    return val;
  }

  // (2) 코드가 없을 때: 상품명으로 매칭. 모델코드(W25EG031) → 한글 키워드 점수매칭 순. 오매칭 방지를 위해 '확실한 단일 후보'만 자동 채택.
  const _optNorm = (s) => String(s == null ? "" : s).replace(/[\[\]()\s]/g, "").toLowerCase();
  /** 옵션 글자에 "2SET"·"3SET" 같은 묶음 표기가 있으면 그 숫자를 돌려줍니다(없으면 1). */
  function extractSetMultiplier(text) {
    const m = String(text || "").match(/(\d+)\s*set\b/i);
    const n = m ? parseInt(m[1], 10) : 1;
    return Number.isFinite(n) && n > 1 ? n : 1;
  }
  // 상품군 구분에 도움 안 되는 공통 단어(브랜드·카테고리)는 키워드에서 제외
  const _STOP = new Set(["wbl","더블유아일랜드","더블유비엘","정품","단독","이혈테라피","이혈","테라피","귀침","주얼패치","주얼","패치","세트","set","free","색상","사이즈","type","공장상품명","최단기간","1위","순정","구성"]);
  function _tokens(text) {
    return String(text || "")
      .replace(/[\[\]()（）★⭐#*.,/_\-=:：]/g, " ")
      .toLowerCase().split(/\s+/).map(t => t.trim())
      .filter(t => t.length >= 2 && !_STOP.has(t) && !/^\d+$/.test(t));
  }
  function _narrow(list, name, option) {
    if (!list || !list.length) return null;
    if (list.length === 1) return { productId: list[0].productId, name: list[0].name, option: list[0].option || "" };
    const hay = _optNorm(option) + _optNorm(name);
    const nar = list.filter(c => { const co = _optNorm(c.option); return co && hay.includes(co); });
    if (nar.length === 1) return { productId: nar[0].productId, name: nar[0].name, option: nar[0].option || "" };
    return null;
  }
  async function resolveByName(name, option) {
    // (a) 모델코드 토큰(W25EG031 등)이 있으면 그걸로 먼저
    const model = [...new Set((String(name || "").match(/[A-Za-z]{1,5}\d{2,}[A-Za-z0-9]*/g) || []))];
    for (const tk of model) { const pick = _narrow(await _searchCatalog(tk), name, option); if (pick) return pick; }
    // (b) 한글/일반 키워드 점수 매칭 — 상품군+옵션이 단일로 확정될 때만 자동 채택
    // 상품명이 여러 구성(본체·부속품 등)에서 똑같이 적히고, 실제 구분은 '옵션' 칸에만 있는
    // 경우가 있습니다 (예: 상품명 "비브아 손 마사지기" + 옵션 "구성=어댑터 추가상품"은
    // 실제로는 본체가 아니라 완전히 다른 '어댑터' 상품입니다). 옵션 텍스트도 검색·채점에
    // 함께 반영해야, 어댑터가 후보에 아예 안 들어와서 본체로 잘못 확정되는 걸 막습니다.
    const toks = [...new Set([..._tokens(name), ..._tokens(option)])];
    if (!toks.length) return null;
    const seen = new Map();
    for (const tk of toks.slice(0, 8)) { for (const c of await _searchCatalog(tk)) if (!seen.has(c.productId)) seen.set(c.productId, c); }
    const cands = [...seen.values()];
    if (!cands.length) return null;
    const score = (c) => { const hay = _optNorm(c.name) + _optNorm(c.option); return toks.reduce((a, t) => a + (hay.includes(_optNorm(t)) ? 1 : 0), 0); };
    cands.sort((a, b) => score(b) - score(a));
    const s0 = score(cands[0]), s1 = cands[1] ? score(cands[1]) : 0;
    if (s0 >= 2 && s0 > s1) {
      const tops = cands.filter(c => score(c) === s0);
      return _narrow(tops, name, option);   // 옵션까지 하나로 확정되는 경우만 채택(아니면 null → 수동)
    }
    return null;
  }

  // (3) 한 상품: 학습된 매칭 → 코드 여러 개 → 상품명 순으로 시도
  async function resolveProduct(p) {
    // 0) 사람이 확정해 학습된 매칭이 최우선 (이름+옵션 조합 키 — 옵션 없는 상품만 이름 단독 키로 자동 처리됨)
    const keys = [];
    if (p.rawName) keys.push(memKey(p.rawName, p.option));
    (p.codes || []).forEach(c => keys.push(memKey(c)));
    for (const k of keys) { const mem = MATCH_MEMORY[k]; if (mem && mem.productId) return { productId: mem.productId, name: mem.name || p.name, option: mem.option || p.option, by: "memory" }; }
    // 1) 코드로
    const codes = (p.codes && p.codes.length) ? p.codes : (p.code ? [p.code] : []);
    for (const c of codes) { const r = await resolveCatalogCode(c); if (r) return { productId: r.productId, name: r.name, option: r.option, by: "code" }; }
    // 2) 상품명으로
    const r2 = await resolveByName(p.rawName || p.name, p.option);
    if (r2) return { productId: r2.productId, name: r2.name, option: r2.option, by: "name" };
    return null;
  }

  /**
   * 다중등록 칸은 '배송 양식'과 '참고용 파일'을 함께 받습니다.
   * 파일마다 배송 양식으로 읽어 봐서, 되면 합치고 안 되면 참고용 첨부로 돌립니다(오류 아님).
   * 배송 양식이 여러 장(타부서 발주서 등)이면 전부 하나의 등록 목록으로 합칩니다.
   */
  async function handleBulkUploadMulti(files) {
    const msg = document.getElementById("fBulkMsg");
    const gv = (id) => document.getElementById(id).value.trim();

    // 공통 정보(요청팀·요청자·희망일·유상무상)는 위 폼에서 한 번만 입력받습니다.
    const team = gv("fTeam"), requester = gv("fRequester"), desiredDate = gv("fDesiredDate");
    const bad = (t) => { msg.style.color = "var(--danger)"; msg.textContent = t; };
    if (!team)      { bad("먼저 위에서 '요청팀'을 입력해 주세요."); return; }
    if (!requester) { bad("먼저 위에서 '요청자'를 입력해 주세요."); return; }
    if (!desiredDate) { bad("먼저 위에서 '출고 희망일'을 선택해 주세요."); return; }

    msg.style.color = "";
    msg.textContent = "파일 읽는 중...";

    // 파일마다 배송 양식인지 시도합니다. 실패하면 에러 대신 '참고용 첨부'로 넘어갑니다.
    let orders = [];
    let anyWarnStores = [];
    for (const f of files) {
      try {
        const parsed = await parseBulkFile(f);
        if (parsed && parsed.length) {
          const auto = !!parsed._autoBilling;
          parsed.forEach(o => { o._autoBilling = auto; });
          orders = orders.concat(parsed);
          if (parsed._warnStores) anyWarnStores = anyWarnStores.concat(parsed._warnStores);
        }
      } catch (e) { /* 배송 양식이 아니면 참고용 파일로 취급 — 오류로 안 보여줍니다 */ }
    }

    if (!orders.length) {
      bad(files.length > 1
        ? "올린 파일 중 배송 목록으로 읽을 수 있는 게 없어요. 실제 배송지·상품이 담긴 양식 파일도 함께 올려 주세요."
        : "파일에서 주문건을 찾지 못했어요. 양식을 확인해 주세요.");
      return;
    }

    // ── 칸 분리: 파일이 1개뿐이고 배송지가 한 곳인데 상품이 여러 개면 '합포장' 건입니다.
    //    (여러 파일을 함께 올린 경우는 참고파일이 섞여 있을 수 있어 이 안내를 건너뜁니다)
    if (files.length === 1 && orders.length === 1 && orders[0].products.length >= 2) {
      const who = orders[0].name || orders[0].address || "한 곳";
      msg.style.color = "var(--danger)";
      msg.innerHTML = `이 파일은 배송지가 <b>한 곳</b>이에요 (${esc(who)} · 상품 ${orders[0].products.length}건).<br>
        여기는 <b>배송지가 여러 곳</b>일 때 쓰는 칸이에요. 아래 <b>📎 발주서로 자동 채우기 (선택)</b> 에 올리면
        상품·수령정보를 화면에서 확인하고 고친 뒤 한 요청서로 등록할 수 있어요.
        <button type="button" class="btn small" id="moveToOrderBtn" style="margin-top:8px;">📎 그 칸으로 옮기기</button>`;
      const mb = document.getElementById("moveToOrderBtn");
      if (mb) mb.addEventListener("click", () => {
        fileStore.fBulkFile = [];
        const bi = document.getElementById("fBulkFile"); if (bi) bi.value = "";
        document.getElementById("fBulkFileName").innerHTML = "";
        msg.innerHTML = "";
        moveFilesTo("fOrderFile", files, refreshOrderFile);
      });
      return;
    }

    // 판매가 컬럼이 없는 파일에서 온 줄이 하나라도 있으면, 위 폼의 유상/무상 선택이 필요합니다.
    const needsManualBilling = orders.some(o => !o._autoBilling);
    if (needsManualBilling && !billingValue) {
      bad("먼저 위에서 '유상/무상'을 선택해 주세요. (또는 파일에 '판매가' 열을 넣으면 줄마다 자동 분류돼요.)");
      return;
    }

    // 상품코드로 이지어드민 카탈로그를 조회해 내부코드(S코드)·옵션·정식 상품명을 자동으로 채웁니다.
    // → 옵션이 채워지고, 재고조회용 내부코드가 들어가서 물류대장에서 '가용재고'가 정상 표시돼요.
    msg.textContent = "상품코드 매칭 중...";
    let matched = 0, byName = 0, qtyAdjusted = 0; const unmatched = [];
    for (const o of orders) {
      for (const p of o.products) {
        const origOption = p.option;               // 매칭되면 옵션이 카탈로그 값으로 덮어써지므로, 배수 판단은 원본 옵션으로 먼저 해둡니다.
        const found = await resolveProduct(p);
        if (found) {
          // "2SET"·"3SET"처럼 이지어드민에 전용 코드가 없어 기본 상품 1개로 매칭된 구성묶음은,
          // 실제로 그 개수만큼 나가야 하므로 수량에 배수를 곱합니다.
          // (매칭된 카탈로그 상품 자체가 이미 그 세트를 나타내면— 이름/옵션에 같은 숫자+SET가 있으면 — 곱하지 않습니다)
          const setMul = extractSetMultiplier(origOption);
          const alreadyRepresentsSet = setMul > 1 && _optNorm((found.name || "") + (found.option || "")).includes(setMul + "set");
          if (setMul > 1 && !alreadyRepresentsSet) {
            p.qty = (Number(p.qty) || 1) * setMul;
            p._qtySetMultiplier = setMul;
            qtyAdjusted++;
          }
          p.code = found.productId;                 // 바코드/옵션코드 → 내부코드(S코드)
          if (found.option) p.option = found.option; // 옵션 자동 채움
          if (found.name) p.name = found.name;        // 정식 상품명으로 정리
          p._match = found.by;                        // "code" | "name"
          matched++;
          if (found.by === "name") byName++;
        } else {
          p._match = "none";
          unmatched.push(p.name || (p.codes && p.codes[0]) || "(이름·코드 없음)");
        }
      }
    }
    const totalProducts = orders.reduce((a, o) => a + o.products.length, 0);

    const channels = selectedChannels();
    const channelStr = channels
      .map(c => (c === "기타" && gv("fChannelOther")) ? ("기타:" + gv("fChannelOther")) : c)
      .join(",");
    const assignee = gv("fAssignee") || null;
    const content = gv("fContent") || null;

    msg.textContent = "";
    // 등록 전 '매칭 확인·정정' 화면을 띄웁니다. 실제 등록은 그 화면의 '등록' 버튼에서 실행돼요.
    openBulkReview(orders, {
      team, requester, desiredDate, assignee, content, channelStr,
      billingValue, warnStores: anyWarnStores,
      matched, byName, totalProducts, msgId: "fBulkMsg", fileInputId: "fBulkFile",
      files: files.concat(fileStore.fBulkRefFiles),   // ① 배송 양식 + ② 참고용 서류 — 생성되는 모든 요청서에 함께 첨부
    });
  }

  // 검토 화면에서 '등록'을 누르면 실행되는 실제 등록 루프
  async function submitBulkOrders(orders, ctx) {
    const msg = document.getElementById(ctx.msgId);
    let ok = 0; const failed = [];
    for (let i = 0; i < orders.length; i++) {
      const o = orders[i];
      if (msg) { msg.style.color = ""; msg.textContent = `등록 중... (${i + 1}/${orders.length})`; }
      const payload = {
        requestTeam: ctx.team, requester: ctx.requester, assignee: ctx.assignee,
        itemName: o.products[0].name,
        sku: o.products[0].code || null,
        optionValue: o.products[0].option || null,
        quantity: o.products.reduce((a, p) => a + p.qty, 0),
        wantDate: ctx.desiredDate,
        receivePlace: o.address || null,
        note: ctx.content, scope: "team",
        channels: ctx.channelStr,
        productsJson: JSON.stringify(o.products),
        receiverName: o.name || null,
        receiverPhone: o.phone || null,
        receiverAddress: o.address || null,
        receiverMessage: o.message || null,
        billingType: (o._autoBilling ? o.billing : ctx.billingValue),
      };
      try {
        const res = await fetch(API_CREATE, {
          method: "POST", headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        });
        if (!res.ok) throw new Error("서버 " + res.status);
        ok += 1;
        // 올린 파일 전부(발주서 양식 + 참고용) 를 이 요청서에 자동 첨부
        if (ctx.files && ctx.files.length) {
          try {
            const created = await res.json();
            const srNo2 = created && (created.sr || created.srNo || created.requestId);
            if (srNo2) {
              const fd = new FormData();
              ctx.files.forEach(f => fd.append("files", f, f.name));
              await fetch(API_BASE + "/api/attachments/" + encodeURIComponent(srNo2), { method: "POST", body: fd });
            }
          } catch (e2) { /* 첨부 실패해도 요청 등록은 유지 */ }
        }
      } catch (e) { failed.push(o.name || ("행 " + (i + 1))); }
    }
    await fetchFromServer();
    if (msg) {
      if (failed.length) {
        msg.style.color = "var(--danger)";
        msg.textContent = `${ok}건 등록 완료, ${failed.length}건 실패: ${failed.join(", ")}. 실패 건만 다시 시도해 주세요.`;
      } else {
        msg.style.color = "var(--accent)";
        msg.textContent = `✅ ${ok}건을 모두 등록했어요! '전체 요청'에서 확인하세요.`;
      }
    }
    fileStore.fBulkFile = [];
    const fi = document.getElementById(ctx.fileInputId); if (fi) fi.value = "";
    const fbn = document.getElementById("fBulkFileName"); if (fbn) fbn.innerHTML = "";
    fileStore.fBulkRefFiles = [];
    const frf = document.getElementById("fBulkRefFiles"); if (frf) frf.value = "";
    const frfn = document.getElementById("fBulkRefFilesName"); if (frfn) frfn.innerHTML = "";
  }

  // ===== 대량 임포트: 등록 전 상품 매칭 확인·정정 화면 =====
  let _bulkOrders = null, _bulkCtx = null;
  function _matchBadge(p) {
    if (p._match === "manual") return '<span style="color:#12925a;font-weight:700;">수동선택</span>';
    if (p._match === "memory") return '<span style="color:#12925a;font-weight:700;">학습됨</span>';
    if (p._match === "code")   return '<span style="color:#12925a;font-weight:700;">코드매칭</span>';
    if (p._match === "name")   return '<span style="color:#b8860b;font-weight:700;">상품명추정</span>';
    return '<span style="color:#d33;font-weight:700;">❌ 미매칭</span>';
  }
  function openBulkReview(orders, ctx) {
    _bulkOrders = orders; _bulkCtx = ctx;
    let host = document.getElementById("bulkReviewModal");
    if (!host) { host = document.createElement("div"); host.id = "bulkReviewModal"; document.body.appendChild(host); }
    host.style.display = "block";
    renderBulkReview();
  }
  function closeBulkReview() { const h = document.getElementById("bulkReviewModal"); if (h) h.style.display = "none"; }
  function renderBulkReview() {
    const orders = _bulkOrders, ctx = _bulkCtx, host = document.getElementById("bulkReviewModal");
    if (!host) return;
    const unm = orders.reduce((a, o) => a + o.products.filter(p => !p._match || p._match === "none").length, 0);
    const billLabel = (o) => { const b = o._autoBilling ? o.billing : ctx.billingValue; return b === "paid" ? "유상" : (b === "free" ? "무상" : ""); };
    let rows = "";
    orders.forEach((o, oi) => {
      rows += `<div style="margin-top:14px;padding:8px 10px;background:var(--surface-alt,#f5f5f7);border-radius:8px;font-weight:700;font-size:13px;">${esc(o.name || "(수령자 미상)")} <span style="font-weight:400;color:var(--text-muted);">· ${billLabel(o)} · ${esc((o.address || "").slice(0, 34))}</span></div>`;
      o.products.forEach((p, pi) => {
        const bad = (!p._match || p._match === "none");
        rows += `<div style="padding:8px 6px;border-bottom:1px solid var(--border);${bad ? "background:rgba(221,51,51,.06);" : ""}">
          <div style="display:flex;gap:8px;align-items:center;flex-wrap:wrap;">
            <div style="flex:1 1 240px;min-width:180px;">
              <div style="font-size:13px;">${esc(p.name || "")}</div>
              <div style="font-size:11px;color:var(--text-muted);">코드 ${esc(p.code || "-")} · 옵션 ${esc(p.option || "-")} · 수량 ${p.qty}${p._qtySetMultiplier ? ` <span style="color:var(--accent);font-weight:700;">(${p._qtySetMultiplier}SET → ×${p._qtySetMultiplier} 자동 반영)</span>` : ""} · ${_matchBadge(p)}</div>
            </div>
            <button type="button" class="btn ghost small brFix" data-oi="${oi}" data-pi="${pi}">정정</button>
          </div>
          <div class="brSearch" data-oi="${oi}" data-pi="${pi}" style="display:none;margin-top:6px;">
            <input type="text" class="brQuery" placeholder="상품명·코드로 검색 후 아래에서 선택" style="width:100%;padding:8px;border:1px solid var(--border);border-radius:8px;box-sizing:border-box;background:var(--surface-alt,#fff);color:var(--text);">
            <div class="brResults" style="max-height:170px;overflow:auto;margin-top:4px;"></div>
          </div>
        </div>`;
      });
    });
    host.innerHTML = `
      <div class="brOverlay" style="position:fixed;inset:0;background:rgba(0,0,0,.45);z-index:9998;"></div>
      <div style="position:fixed;z-index:9999;left:50%;top:50%;transform:translate(-50%,-50%);width:min(720px,94vw);max-height:88vh;overflow:auto;background:var(--surface,#fff);color:var(--text);border-radius:14px;padding:18px;box-shadow:0 20px 60px rgba(0,0,0,.35);">
        <div style="display:flex;justify-content:space-between;align-items:center;">
          <strong style="font-size:16px;">📋 등록 전 상품 매칭 확인</strong>
          <button type="button" class="btn ghost small brClose">닫기 ✕</button>
        </div>
        <div style="font-size:12px;color:var(--text-muted);margin:6px 0 2px;">요청팀 ${esc(ctx.team)} · 요청자 ${esc(ctx.requester)} · 희망일 ${esc(ctx.desiredDate)} · 총 ${ctx.totalProducts}개 상품</div>
        ${unm ? `<div style="font-size:12px;color:#d33;font-weight:600;margin:4px 0;">⚠️ 미매칭 ${unm}개 — '정정'을 눌러 올바른 상품을 골라주세요. (미매칭이어도 등록은 되지만 재고가 안 잡혀요)</div>` : `<div style="font-size:12px;color:#12925a;font-weight:600;margin:4px 0;">✅ 모든 상품이 매칭됐어요. 확인 후 등록하세요.</div>`}
        ${(ctx.warnStores && ctx.warnStores.length) ? `<div style="font-size:12px;color:#b8860b;margin:4px 0;">⚠️ 주소 못 찾은 매장: ${esc(ctx.warnStores.join(", "))}</div>` : ""}
        <div>${rows}</div>
        <div style="display:flex;gap:8px;justify-content:flex-end;margin-top:14px;position:sticky;bottom:-18px;background:var(--surface,#fff);padding:10px 0 2px;">
          <button type="button" class="btn ghost brClose">취소</button>
          <button type="button" class="btn" id="brSubmit">이대로 ${orders.length}건 등록</button>
        </div>
      </div>`;
    host.querySelector(".brOverlay").addEventListener("click", closeBulkReview);
    host.querySelectorAll(".brClose").forEach(b => b.addEventListener("click", closeBulkReview));
    host.querySelector("#brSubmit").addEventListener("click", () => { closeBulkReview(); submitBulkOrders(_bulkOrders, _bulkCtx); });
    host.querySelectorAll(".brFix").forEach(b => b.addEventListener("click", () => {
      const oi = +b.dataset.oi, pi = +b.dataset.pi;
      const box = host.querySelector(`.brSearch[data-oi="${oi}"][data-pi="${pi}"]`);
      const open = box.style.display === "none";
      box.style.display = open ? "block" : "none";
      if (open) { const inp = box.querySelector(".brQuery"); inp.value = _bulkOrders[oi].products[pi].name || ""; inp.focus(); runBrSearch(oi, pi); }
    }));
    host.querySelectorAll(".brQuery").forEach(inp => {
      let t; inp.addEventListener("input", () => { const box = inp.closest(".brSearch"); clearTimeout(t); t = setTimeout(() => runBrSearch(+box.dataset.oi, +box.dataset.pi), 300); });
    });
  }
  async function runBrSearch(oi, pi) {
    const host = document.getElementById("bulkReviewModal");
    const box = host.querySelector(`.brSearch[data-oi="${oi}"][data-pi="${pi}"]`);
    const res = box.querySelector(".brResults");
    const q = box.querySelector(".brQuery").value.trim();
    if (!q) { res.innerHTML = ""; return; }
    res.innerHTML = '<div style="font-size:12px;color:var(--text-muted);padding:6px;">검색 중…</div>';
    const list = await _searchCatalog(q);
    if (!list.length) { res.innerHTML = '<div style="font-size:12px;color:var(--text-muted);padding:6px;">결과 없음 — 다른 검색어를 입력해 보세요.</div>'; return; }
    box._cands = list;
    res.innerHTML = list.slice(0, 20).map((c, i) => `<div class="brPick" data-i="${i}" style="padding:7px 8px;border:1px solid var(--border);border-radius:8px;margin-bottom:4px;cursor:pointer;font-size:12px;">
        <b>${esc(c.name || "")}</b> <span style="color:var(--text-muted);">${esc(c.option || "")}</span><br><span style="color:var(--accent);">${esc(c.productId || "")}</span>${c.available != null ? ` · 가용 ${c.available}` : ""}
      </div>`).join("");
    res.querySelectorAll(".brPick").forEach(el => el.addEventListener("click", () => {
      const c = box._cands[+el.dataset.i];
      const p = _bulkOrders[oi].products[pi];
      p.code = c.productId; p.option = c.option || p.option; p.name = c.name || p.name; p._match = "manual";
      saveMatchMemory(p, c);   // 이 정정을 학습 저장 → 다음부터 자동 적용
      renderBulkReview();
    }));
  }

  /* =========================================================================
   * 스마트 유니버설 임포트 — 타팀에서 어떤 양식으로 보내도 우리 요청서로 매칭
   *  · 헤더가 몇 번째 줄에 있든 자동 탐지
   *  · 컬럼 이름이 달라도 별칭 사전으로 우리 필드에 매핑
   *  · 매장/수령자가 빈 칸이면 위 줄 값을 이어받음(forward-fill)
   *  · 소계/합계/빈 서식줄은 건너뜀
   *  · 매장명만 있고 주소가 없으면 우리 주소록(OFFBEAUTY_STORES)에서 주소·연락처 자동 채움
   *  · 수량은 출고수량 > 수주수량 > 수량 순으로 인식
   * ======================================================================= */
  const IMPORT_ALIASES = {
    name:   ["수령자이름","수령자 이름","수령자명","수령인","수령인명","수취인","수취인명","받는분","받는사람","받으실분","고객명","성명","담당자명","담당자","받는이","수하인","수하인명"],
    store:  ["매장","매장명","점포명","점명","지점","지점명","판매처","거래처","거래처명","납품처","배송처","도착지","출고처","점포"],
    phoneMobile: ["핸드폰","휴대폰","수령인핸드폰번호","수취인핸드폰번호","수취인휴대폰","담당자핸드폰","고객핸드폰","핸폰","휴대전화","모바일"],
    phone:  ["수령자연락처","수령자 연락처","수령인연락처","수령인전화번호","연락처","전화번호","전화","수취인연락처","수취인전화번호","받는분연락처","매장연락처","담당자연락처"],
    zip:    ["수령자우편번호","우편번호","우편","zip"],
    addr:   ["수령자주소","수령자 주소","수령인주소","주소","배송지","배송주소","받는주소","수취인주소","도착지주소","매장주소","담당자주소"],
    addr2:  ["상세주소","나머지주소","상세 주소","상세주소2","참고주소"],
    msg:    ["배송메세지","배송메시지","배송 메세지","배송요청","배송요청사항","요청사항","배송메모","전달사항","배송시요청사항"],
    pname:  ["상품명","제품명","품명","상품","품목","품목명","상품이름","공장상품명"],
    popt:   ["옵션","옵션명","옵션정보","옵션값","색상","사이즈","규격"],
    qtyOut: ["출고수량","출고 수량","출고량"],
    qtyOrd: ["수주수량","주문수량","발주수량","요청수량","입고예정수량","입고수량","신청수량","발송수량"],
    qty:    ["수량","개수","주문수량합계","수량합계","ea"],
    code:   ["상품코드","상품 코드","코드","품번","품목코드","제품코드","sku","sku번호","바코드","상품번호","옵션코드","옵션 코드","바코드번호","제품바코드","품목바코드","단품코드","자체코드","자체바코드","모델코드","판매자옵션코드","판매자상품코드","대표납품서바코드"],
    orderNo:["주문번호","주문 번호","오더번호","주문no"],
    orderDate:["주문일","주문일자","발주일","주문날짜"],
    orderTime:["주문시간","주문 시간"],
    price:  ["판매가","단가","가격","금액"],
  };
  const impNorm = (s) => String(s == null ? "" : s).replace(/[\s()（）\[\]{}<>:：·.,\-_/]/g, "").toLowerCase();

  /** 매장명을 우리 주소록의 표준 매장으로 매칭(정규화 후 부분일치) */
  function matchStore(rawName) {
    if (!rawName) return null;
    const clean = (x) => String(x || "")
      .replace(/오프뷰티|off\s*beauty|offbeauty/gi, "")
      .replace(/메가|팩토리|프리미엄|아울렛|스퀘어|점포|매장/g, "")
      .replace(/[\s()（）\[\]·.,\-_/]/g, "")
      .replace(/점$/, "")
      .toLowerCase();
    const target = clean(rawName);
    if (!target) return null;
    let best = null, bestLen = -1;
    for (const s of OFFBEAUTY_STORES) {
      const c = clean(s.name);
      if (!c) continue;
      if (c === target) return s;                                  // 완전 일치 우선
      if ((c.includes(target) || target.includes(c)) && c.length > bestLen) { best = s; bestLen = c.length; }
    }
    return best;
  }

  /** 다중배송/타팀 양식을 읽어 수령자(또는 매장)별로 묶습니다.
   *  → [{name,phone,address,message,storeMatched,products:[{code,name,option,qty}]}] */
  async function parseBulkFile(file) {
    const buf = await file.arrayBuffer();
    const wb = XLSX.read(buf, { type: "array" });
    const rows = XLSX.utils.sheet_to_json(wb.Sheets[wb.SheetNames[0]], { header: 1, defval: "" });
    if (!rows.length) throw new Error(file.name + ": 빈 파일이에요.");

    // 1) 헤더행 탐지 — 상위 25줄 중 별칭과 가장 많이 겹치는 줄을 헤더로 봄
    const flatAlias = {};
    for (const k in IMPORT_ALIASES) for (const a of IMPORT_ALIASES[k]) flatAlias[impNorm(a)] = k;
    const scoreRow = (r) => {
      const seen = new Set();
      (r || []).forEach(c => { const key = flatAlias[impNorm(c)]; if (key) seen.add(key); });
      return seen.size;
    };
    let headerIdx = 0, headerScore = -1;
    const scanTo = Math.min(rows.length, 40);
    for (let i = 0; i < scanTo; i++) {
      const s = scoreRow(rows[i]);
      if (s > headerScore) { headerScore = s; headerIdx = i; }
    }
    if (headerScore < 2) throw new Error(file.name + ": 표의 제목줄(상품명·수량·주소 등)을 찾지 못했어요. 양식을 확인해 주세요.");

    // 2) 헤더 → 우리 필드로 매핑(같은 필드에 여러 열이면 첫 번째만)
    const header = rows[headerIdx];
    const col = {};
    header.forEach((c, idx) => { const key = flatAlias[impNorm(c)]; if (key && col[key] === undefined) col[key] = idx; });
    // 코드가 여러 칸(상품코드+바코드+옵션코드 등)에 흩어져 있을 수 있어, '코드'로 인식된 모든 열을 모아둡니다.
    const codeCols = [];
    header.forEach((c, idx) => { if (flatAlias[impNorm(c)] === "code") codeCols.push(idx); });
    // 받는 사람을 특정할 열이 하나도 없으면(상품코드·상품명만 있는 '상품 목록' 파일 등),
    // 배송 양식이 아니라 참고용 파일입니다. 빈 수령정보로 요청서를 만들지 않도록 여기서 막습니다.
    if (col.name === undefined && col.addr === undefined && col.store === undefined) {
      throw new Error(file.name + ": 받는 사람 정보(수령자·주소·매장)를 찾지 못했어요. 배송 양식이 아닌 것 같아요.");
    }

    const get = (r, key) => (col[key] === undefined ? "" : String(r[col[key]] == null ? "" : r[col[key]]).trim());
    const pickQty = (r) => {
      for (const key of ["qtyOut", "qtyOrd", "qty"]) {
        if (col[key] === undefined) continue;
        const n = parseInt(String(get(r, key)).replace(/[^\d-]/g, ""), 10);
        if (Number.isFinite(n) && n > 0) return n;
      }
      return 0;
    };
    // 판매가 컬럼이 있으면 줄마다 유상/무상을 자동 판별합니다(값이 있으면 유상, 비어 있으면 무상).
    const hasPriceCol = col.price !== undefined;
    const parsePrice = (r) => {
      if (!hasPriceCol) return 0;
      const n = parseFloat(String(get(r, "price")).replace(/[^\d.]/g, ""));
      return Number.isFinite(n) && n > 0 ? n : 0;
    };

    // 3) 데이터행 순회 — 매장/수령자 forward-fill, 소계·빈줄 스킵
    const groups = new Map();
    let carryStore = "", carryName = "", carryPhone = "", carryAddr = "", carryAddr2 = "", carryMsg = "";
    const warnStores = new Set();

    for (let i = headerIdx + 1; i < rows.length; i++) {
      const r = rows[i] || [];
      const rowText = r.map(v => String(v || "").trim()).join("");
      if (!rowText) continue;                                          // 완전 빈 줄

      // 매장/수령자/주소 등은 병합셀 특성상 첫 줄에만 있는 경우가 많아 이어받음
      const store = get(r, "store"); if (store) carryStore = store;
      const nm    = get(r, "name");  if (nm)    carryName = nm;
      const ph    = get(r, "phoneMobile") || get(r, "phone"); if (ph) carryPhone = ph;
      const ad    = get(r, "addr");  if (ad)    carryAddr = ad;
      // "상세주소"(동/호수 등)가 따로 한 칸 더 있는 양식(현대M몰 등)이 있어서, 있으면 주소 뒤에 붙입니다.
      // 안 붙이면 동·호수가 통째로 빠져서 배송 사고로 이어질 수 있습니다.
      const ad2   = get(r, "addr2"); if (ad2)   carryAddr2 = ad2;
      const mg    = get(r, "msg");   if (mg)    carryMsg = mg;

      const pname = get(r, "pname");
      // 이 줄의 코드들(여러 칸이면 전부, 중복 제거). 첫 번째를 대표 code로 둠.
      const codeVals = [...new Set(codeCols.map(ci => String(r[ci] == null ? "" : r[ci]).trim()).filter(Boolean))];
      const code  = codeVals[0] || "";
      const qty   = pickQty(r);
      // 상품명도 코드도 없으면 상품 줄이 아님(소계/합계/구분선) → 건너뜀
      if (!pname && !code) continue;
      // 소계/합계/총계 등의 안내문이 상품명칸에 들어온 경우 제외
      if (/소계|합계|총계|합\s*계|subtotal|total/i.test(pname)) continue;

      // 이 줄의 판매가로 유상/무상 자동 판별(판매가 컬럼이 있을 때만). 없으면 "" → 위 폼의 수동 선택을 씀
      const price = parsePrice(r);
      const rowBilling = hasPriceCol ? (price > 0 ? "paid" : "free") : "";

      // 수령자 결정: 주소가 직접 있으면 그 수령자, 없고 매장만 있으면 주소록에서 채움
      let name = carryName, phone = carryPhone;
      let address = carryAddr2 ? [carryAddr, carryAddr2].filter(Boolean).join(" ") : carryAddr;
      let storeMatched = true;
      if (!address && carryStore) {
        const s = matchStore(carryStore);
        if (s) { name = name || s.name; phone = phone || s.phone; address = s.addr; }
        else   { name = name || carryStore; storeMatched = false; warnStores.add(carryStore); }
      } else if (!name && carryStore) {
        name = carryStore;
      }
      const zip = get(r, "zip");
      if (zip && address && !address.startsWith("(")) address = `(${zip}) ${address}`;

      // 같은 수령자라도 유상/무상이 다르면 요청서를 나눠서 만듭니다(한 파일에 섞여 있어도 자동 분리).
      const groupKey = (carryStore || "") + "|" + name + "|" + phone + "|" + address + "|" + rowBilling;
      if (!groups.has(groupKey)) {
        groups.set(groupKey, { name, phone, address, message: carryMsg, storeMatched, billing: rowBilling, products: [] });
      }
      groups.get(groupKey).products.push({
        code, codes: codeVals, name: pname || code, rawName: pname || code,
        option: get(r, "popt") || "없음", qty: qty > 0 ? qty : 1,
        price: price > 0 ? price : "",
      });
    }

    const result = [...groups.values()].filter(g => g.products.length);
    if (!result.length) throw new Error(file.name + ": 표는 찾았지만 등록할 상품 줄이 없어요. 상품명/수량이 있는지 확인해 주세요.");

    // 표에 수령자 칸이 아예 없는 '양식 문서'(발주서 등)라면, 표 밖 항목-값 칸에서 찾아 채웁니다.
    if (result.every(g => !g.name && !g.phone && !g.address)) {
      const grid = rows.map(r => (r || []).map(v => String(v == null ? "" : v).trim()));
      const meta = extractReceiverMeta(grid, headerIdx);
      if (meta.name || meta.addr || meta.phone) {
        const pm = String(meta.phone || "").match(RE_PHONE);
        result.forEach(g => {
          g.name    = g.name    || meta.name || meta.store || "";
          g.phone   = g.phone   || (pm ? (pm[1] + "-" + pm[2] + "-" + pm[3]) : (meta.phone || ""));
          g.address = g.address || meta.addr || "";
          g.message = g.message || meta.msg  || "";
        });
        result._metaFilled = true;
      }
    }
    result._warnStores = [...warnStores];   // 주소를 못 찾은 매장(참고용)
    result._autoBilling = hasPriceCol;      // 판매가 컬럼이 있으면 줄마다 유상/무상이 자동 판별됨
    return result;
  }

  document.getElementById("dlFreeTemplate").addEventListener("click", () => downloadBulkTemplate("free"));
  document.getElementById("dlPaidTemplate").addEventListener("click", () => downloadBulkTemplate("paid"));
  /** fBulkFile 저장소가 바뀔 때마다: 칩을 다시 그리고, 쌓인 파일 전체로 다시 읽습니다. */
  function refreshBulkFiles() {
    const store = fileStore.fBulkFile;
    renderFileChips(document.getElementById("fBulkFileName"), store, (i) => {
      removeFileFromStore("fBulkFile", i);
      refreshBulkFiles();
    });
    if (store.length) handleBulkUploadMulti(store);
    else document.getElementById("fBulkMsg").textContent = "";
  }
  document.getElementById("fBulkFile").addEventListener("change", (e) => {
    addFilesToStore("fBulkFile", e.target.files);
    refreshBulkFiles();
  });

  /** fAttachFiles 저장소가 바뀔 때마다 칩을 다시 그립니다. */
  function refreshAttachFiles() {
    renderFileChips(document.getElementById("fAttachList"), fileStore.fAttachFiles, (i) => {
      removeFileFromStore("fAttachFiles", i);
      refreshAttachFiles();
    });
  }
  // 첨부파일 칸에 파일을 고르면(또는 끌어다 놓으면), 기존에 고른 파일들에 더합니다.
  document.getElementById("fAttachFiles").addEventListener("change", (e) => {
    addFilesToStore("fAttachFiles", e.target.files);
    refreshAttachFiles();
  });

  document.getElementById("addProductBtn").addEventListener("click", () => addProductRow(true));

  // [수량 일괄 등록] '전체 선택' 체크박스 — 지금 있는 모든 상품 줄의 선택칸을 한 번에 켜고 끕니다.
  document.getElementById("pPickAll").addEventListener("change", (e) => {
    document.querySelectorAll("#productRows .pPick").forEach(cb => { cb.checked = e.target.checked; });
  });
  // 개별 선택칸을 손대면 '전체 선택' 상태도 맞춰 갱신합니다.
  document.getElementById("productRows").addEventListener("change", (e) => {
    if (!e.target.classList.contains("pPick")) return;
    const all = [...document.querySelectorAll("#productRows .pPick")];
    const allOn = all.length > 0 && all.every(cb => cb.checked);
    document.getElementById("pPickAll").checked = allOn;
  });
  // [수량 일괄 등록] '일괄 등록' 버튼 — 체크한 상품(없으면 전체)에 같은 수량을 채웁니다.
  document.getElementById("pBulkApplyBtn").addEventListener("click", () => {
    const raw = document.getElementById("pBulkQty").value.trim();
    const qty = Number(raw);
    if (!raw || !Number.isFinite(qty) || qty <= 0) {
      showBanner("일괄 등록할 수량을 1 이상의 숫자로 입력해 주세요.", "error");
      document.getElementById("pBulkQty").focus();
      return;
    }
    const rows = [...document.querySelectorAll("#productRows .prow")];
    const picked = rows.filter(r => { const cb = r.querySelector(".pPick"); return cb && cb.checked; });
    const targets = picked.length ? picked : rows;   // 아무것도 선택 안 했으면 전체 적용
    targets.forEach(r => { const q = r.querySelector(".pQty"); if (q) { q.value = qty; q.classList.remove("invalid"); } });
    showBanner(`선택한 ${targets.length}개 상품에 수량 ${qty}(을)를 채웠어요.`, "ok");
    setTimeout(() => showBanner(""), 2500);
  });

  // "재고확인" 버튼 — 화면 배지와 상관없이, 누르면 그 자리에서 바로 결과를 팝업으로 보여줍니다.
  document.getElementById("productRows").addEventListener("click", async (e) => {
    const btn = e.target.closest(".stockCheckBtn");
    if (!btn) return;
    const row = btn.closest(".prow");
    const code = row.querySelector(".pCode").value.trim();
    if (!code) { alert("상품코드를 먼저 입력해 주세요."); return; }

    btn.disabled = true;
    const originalText = btn.textContent;
    btn.textContent = "조회 중...";
    try {
      const res = await fetch(API_STOCK + "?codes=" + encodeURIComponent(code));
      if (!res.ok) throw new Error("서버 응답 " + res.status);
      const data = await res.json();
      const s = data[code];
      if (!s) {
        alert(`[${code}] 이지어드민에 등록되지 않은 코드예요.`);
      } else {
        alert(
          `[${code}] 재고 조회 결과\n\n` +
          `현재고: ${s.stock}개\n` +
          (s.readyTransStock != null ? `접수/송장: ${s.readyTransStock}개\n` : "") +
          (s.available != null ? `가용재고: ${s.available}개` : "")
        );
      }
    } catch (err) {
      alert("재고 조회에 실패했어요: " + (err.message || "연결 오류"));
    } finally {
      btn.disabled = false;
      btn.textContent = originalText;
    }
  });
  document.querySelectorAll("#billingSeg .segbtn").forEach(btn => {
    btn.addEventListener("click", () => {
      billingValue = btn.dataset.val;
      document.querySelectorAll("#billingSeg .segbtn").forEach(b => {
        const on = b === btn;
        b.classList.toggle("sel", on);
        b.setAttribute("aria-pressed", String(on));
      });
      document.getElementById("billingSeg").classList.remove("invalid");
      const el = document.querySelector('[data-err="billing"]');
      if (el) el.textContent = "";
    });
  });
  document.querySelectorAll("#reqTypeSeg .segbtn").forEach(btn => {
    btn.addEventListener("click", () => setReqType(btn.dataset.reqtype));
  });
  document.getElementById("submitFormBtn").addEventListener("click", submitForm);

  // 수령자 이름을 입력하면(특히 자동완성에서 고르면), 예전에 썼던 연락처·주소·메세지를 자동으로 채웁니다.
  // 이미 뭔가 적어 둔 칸은 덮어쓰지 않습니다 — 빈 칸만 채웁니다.
  document.getElementById("fRcvName").addEventListener("input", (e) => {
    const val = e.target.value.trim();
    const setIfEmpty = (id, v) => {
      const el = document.getElementById(id);
      if (v && !el.value.trim()) el.value = v;
    };
    // 1) 오프뷰티 매장명과 정확히 일치하면 주소·연락처 자동완성
    const store = OFFBEAUTY_STORES.find(s => s.name === val);
    if (store) {
      setIfEmpty("fRcvPhone", store.phone);
      setIfEmpty("fRcvAddr", store.addr);
      return;
    }
    // 2) 이전에 썼던 수령정보 자동완성
    const hit = RECEIVER_HISTORY[val];
    if (!hit) return;
    setIfEmpty("fRcvPhone", hit.phone);
    setIfEmpty("fRcvAddr", hit.address);
    setIfEmpty("fRcvMsg", hit.message);
  });
  document.getElementById("resetFormBtn").addEventListener("click", () => resetForm(false));

  function exportLocalData() {
    if (!LOCAL.length) { alert("내보낼 로컬 항목이 없어요. 요청서 작성으로 등록한 항목만 내보낼 수 있어요."); return; }
    const blob = new Blob([JSON.stringify(LOCAL, null, 2)], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `wr-ledger-local-${TODAY}.json`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }

  function importLocalData(file) {
    const reader = new FileReader();
    reader.onload = () => {
      let incoming;
      try { incoming = JSON.parse(reader.result); }
      catch (e) { alert("파일을 읽을 수 없어요. 내보내기로 받은 JSON 파일이 맞는지 확인해 주세요."); return; }
      if (!Array.isArray(incoming)) { alert("올바른 내보내기 파일이 아니에요."); return; }
      const existingIds = new Set(DATA.map(r => r.requestId));
      let added = 0, skipped = 0;
      incoming.forEach(item => {
        if (!item || !item.requestId || existingIds.has(item.requestId)) { skipped++; return; }
        LOCAL.push(item);
        existingIds.add(item.requestId);
        added++;
      });
      saveLocal();
      refreshAll();
      alert(`가져오기 완료 — 새로 추가 ${added}건${skipped ? `, 이미 있어서 건너뜀 ${skipped}건` : ""}`);
    };
    reader.readAsText(file);
  }

  document.getElementById("exportLocalBtn").addEventListener("click", exportLocalData);
  document.getElementById("importLocalBtn").addEventListener("click", () => document.getElementById("importFileInput").click());
  document.getElementById("importFileInput").addEventListener("change", (e) => {
    const file = e.target.files[0];
    if (file) importLocalData(file);
    e.target.value = "";
  });

  document.getElementById("sidebarUpdated").textContent = TODAY;
  document.getElementById("topUpdated").textContent = TODAY;

  buildChannelChecks();
  resetForm(false);
  refreshAll();

  fetchFromServer();
  setInterval(fetchFromServer, 30000);
  loadMatchMemory();   // 학습된 상품매칭 불러오기(자동매칭에 최우선 적용)

  function setStoreFiles(inputId, files) {
    const input = document.getElementById(inputId);
    const dt = new DataTransfer();
    files.forEach(f => dt.items.add(f));
    input.files = dt.files;
    fileStore[inputId] = files;
  }

  /** 새로 고르거나 끌어다 놓은 파일을 기존 목록에 더합니다 (같은 파일 중복 추가는 건너뜀). */
  function addFilesToStore(inputId, newFiles) {
    const store = fileStore[inputId] || [];
    const seen = new Set(store.map(fileKey));
    const merged = store.slice();
    Array.from(newFiles || []).forEach(f => {
      const k = fileKey(f);
      if (!seen.has(k)) { merged.push(f); seen.add(k); }
    });
    setStoreFiles(inputId, merged);
    return merged;
  }

  /** ×로 하나만 뺍니다. */
  function removeFileFromStore(inputId, index) {
    const store = (fileStore[inputId] || []).slice();
    store.splice(index, 1);
    setStoreFiles(inputId, store);
    return store;
  }

  /** 파일들을 '이름 + × 삭제' 칩으로 보여줍니다. onRemove(index) 가 있으면 × 버튼이 생깁니다. */
  function renderFileChips(container, files, onRemove) {
    const list = Array.from(files || []);
    if (!list.length) { container.innerHTML = ""; return; }
    container.innerHTML = list.map((f, i) =>
      `<span class="filechip"><span class="fc-name">${esc(f.name)}</span>`
      + (onRemove ? `<button type="button" class="fc-x" data-i="${i}" title="이 파일 빼기" aria-label="${esc(f.name)} 삭제">×</button>` : "")
      + `</span>`
    ).join("");
    if (!onRemove) return;
    container.querySelectorAll(".fc-x").forEach(btn => {
      btn.addEventListener("click", () => onRemove(+btn.dataset.i));
    });
  }

  // ===== 드래그앤드롭 업로드 배너 =====
  // 배너(label) 자체가 드롭존입니다. 클릭하면 <label for="..."> 가 알아서 파일 선택창을 엽니다.
  // 파일을 끌어다 놓으면 '방금 놓은 파일'만 그 input 에 실어서 change 를 태웁니다.
  // (기존 목록과 합치는 일은 각 change 리스너 쪽에서 addFilesToStore 로 처리합니다)
  function enableDropZone(inputId, dropId) {
    const input = document.getElementById(inputId);
    const drop = document.getElementById(dropId);
    if (!input || !drop) return;
    const stop = (e) => { e.preventDefault(); e.stopPropagation(); };

    ["dragenter", "dragover"].forEach(ev => drop.addEventListener(ev, (e) => { stop(e); drop.classList.add("dragover"); }));
    ["dragleave", "dragend"].forEach(ev => drop.addEventListener(ev, (e) => { stop(e); drop.classList.remove("dragover"); }));
    drop.addEventListener("drop", (e) => {
      stop(e); drop.classList.remove("dragover");
      const files = e.dataTransfer && e.dataTransfer.files;
      if (!files || !files.length) return;
      try {
        const dt = new DataTransfer();
        // multiple 이 없는 입력칸(다중등록 양식)은 첫 파일만 받습니다.
        const list = input.multiple ? Array.from(files) : [files[0]];
        for (const f of list) dt.items.add(f);
        input.files = dt.files;
      } catch (err) { /* 일부 브라우저 미지원 시 클릭 업로드로 대체 */ }
      input.dispatchEvent(new Event("change", { bubbles: true }));
    });
  }
  enableDropZone("fBulkFile", "fBulkDrop");
  enableDropZone("fBulkRefFiles", "fBulkRefDrop");
  enableDropZone("fOrderFile", "fOrderDrop");
  enableDropZone("fOrderRefFiles", "fOrderRefDrop");
  enableDropZone("fAttachFiles", "fAttachDrop");

  // 참고용 서류 배너 — 파싱하지 않고 그대로 쌓아 두었다가, 요청서 생성 시 함께 첨부합니다.
  // (올리든 ×로 빼든) 바뀔 때마다 칩을 다시 그리고, 이미 만들어져 있는 첨부 목록도 조용히 맞춰 둡니다.
  // 발주서를 다시 읽거나 검토 화면을 다시 그리지는 않습니다 — 그러면 이미 채운 상품 정보나
  // 검토 화면에서 고친 내용이 날아가 버립니다.
  function refreshRefFiles(inputId, containerId) {
    renderFileChips(document.getElementById(containerId), fileStore[inputId], (i) => {
      removeFileFromStore(inputId, i);
      refreshRefFiles(inputId, containerId);
    });
    if (inputId === "fBulkRefFiles" && _bulkCtx) {
      _bulkCtx.files = fileStore.fBulkFile.concat(fileStore.fBulkRefFiles);
    }
    if (inputId === "fOrderRefFiles") {
      orderFilesForAttach = fileStore.fOrderFile.concat(fileStore.fOrderRefFiles);
    }
  }
  document.getElementById("fBulkRefFiles").addEventListener("change", (e) => {
    addFilesToStore("fBulkRefFiles", e.target.files);
    refreshRefFiles("fBulkRefFiles", "fBulkRefFilesName");
  });
  document.getElementById("fOrderRefFiles").addEventListener("change", (e) => {
    addFilesToStore("fOrderRefFiles", e.target.files);
    refreshRefFiles("fOrderRefFiles", "fOrderRefFilesName");
  });

  /* =========================================================================
   * 엑셀 작업실 — 발주서·송장 파일 등을 올려서 VLOOKUP 매칭·정렬·중복제거·
   * 계산열 추가 같은 실무 작업을 하고 그대로 내려받습니다. (2026-09-07)
   * 데이터는 시트를 그대로(A,B,C… 열 문자·1,2,3… 행 번호) 보여줘서, 파일마다
   * 제목줄 위치가 달라도 사람이 직접 보고 골라 쓸 수 있게 했습니다(자동 추정 안 함).
   * ======================================================================= */
  let XT_FILES = [];   // { id, name, aoa, headerRow, colSel:Set, rowSel:Set, filter:null|{col,text}, history:[aoa...], noMatchCells:Set|null }
  let XT_ACTIVE = -1;

  function xtColLetter(i) {
    let s = "", n = i + 1;
    while (n > 0) { const m = (n - 1) % 26; s = String.fromCharCode(65 + m) + s; n = Math.floor((n - 1) / 26); }
    return s;
  }
  function xtLetterToIdx(s) {
    s = String(s || "").trim().toUpperCase();
    if (!/^[A-Z]+$/.test(s)) return null;
    let n = 0;
    for (const ch of s) n = n * 26 + (ch.charCodeAt(0) - 64);
    return n - 1;
  }
  function xtNormKey(v) { return String(v == null ? "" : v).trim().replace(/\s+/g, "").toLowerCase(); }
  function xtActiveFile() { return XT_ACTIVE >= 0 ? XT_FILES[XT_ACTIVE] : null; }
  function xtColCount(f) { return f.aoa.reduce((m, row) => Math.max(m, row ? row.length : 0), 1); }
  function xtColLabel(f, colIdx) {
    const letter = xtColLetter(colIdx);
    const hv = f.aoa[f.headerRow - 1] ? String(f.aoa[f.headerRow - 1][colIdx] ?? "").trim() : "";
    return hv ? `${letter} (${hv.length > 16 ? hv.slice(0, 16) + "…" : hv})` : letter;
  }
  function xtColOptsHtml(f) {
    const n = xtColCount(f); let s = "";
    for (let c = 0; c < n; c++) s += `<option value="${c}">${esc(xtColLabel(f, c))}</option>`;
    return s;
  }

  async function xtReadFile(file) {
    const buf = await file.arrayBuffer();
    const wb = XLSX.read(new Uint8Array(buf), { type: "array" });
    const ws = wb.Sheets[wb.SheetNames[0]];
    const aoa = XLSX.utils.sheet_to_json(ws, { header: 1, defval: "", blankrows: true, range: 0 })
      .map(row => (row || []).map(v => v == null ? "" : v));
    // 원본 파일 서식이 실제 데이터보다 훨씬 아래 행까지 걸려 있는 경우가 많아서,
    // 맨 끝의 완전히 빈 행들만 잘라냅니다(중간 빈 줄은 원본 그대로 둡니다).
    while (aoa.length > 1 && !aoa[aoa.length - 1].some(v => String(v || "").trim())) aoa.pop();
    return { id: "xt" + Date.now() + Math.random().toString(36).slice(2, 6), name: file.name, aoa,
      headerRow: 1, colSel: new Set(), rowSel: new Set(), filter: null, history: [], noMatchCells: null };
  }
  async function xtAddFiles(fileList) {
    for (const f of Array.from(fileList)) {
      try { XT_FILES.push(await xtReadFile(f)); XT_ACTIVE = XT_FILES.length - 1; }
      catch (e) { alert(f.name + ": 파일을 읽지 못했어요 (" + (e.message || "") + ")"); }
    }
    xtRenderAll();
  }
  function xtPushHistory() {
    const f = xtActiveFile(); if (!f) return;
    f.history.push(JSON.parse(JSON.stringify(f.aoa)));
    if (f.history.length > 20) f.history.shift();
    f.noMatchCells = null;   // 행이 움직이면 예전 '매칭 안 됨' 표시는 더 이상 맞지 않으니 지웁니다
  }
  function xtUndo() {
    const f = xtActiveFile(); if (!f || !f.history.length) return;
    f.aoa = f.history.pop();
    f.noMatchCells = null;
    xtRenderAll();
    xtSetStatus("실행을 취소했어요.");
  }
  function xtSetStatus(msg, cls) {
    const el = document.getElementById("xtStatus");
    el.textContent = msg || "";
    el.className = "xt-status" + (cls ? " " + cls : "");
  }

  function xtRenderAll() { xtRenderFileTabs(); xtRenderToolbarState(); xtRenderGrid(); }

  function xtRenderFileTabs() {
    const el = document.getElementById("xtFileTabs");
    el.innerHTML = XT_FILES.map((f, i) => `
      <span class="xt-filetab${i === XT_ACTIVE ? " active" : ""}" data-i="${i}">
        📄 ${esc(f.name)}
        <button type="button" class="x" data-close="${i}" title="닫기">✕</button>
      </span>`).join("");
    el.querySelectorAll(".xt-filetab").forEach(t => t.addEventListener("click", (e) => {
      if (e.target.closest("[data-close]")) return;
      XT_ACTIVE = Number(t.dataset.i);
      xtRenderAll();
    }));
    el.querySelectorAll("[data-close]").forEach(b => b.addEventListener("click", (e) => {
      e.stopPropagation();
      const i = Number(b.dataset.close);
      XT_FILES.splice(i, 1);
      if (XT_ACTIVE >= XT_FILES.length) XT_ACTIVE = XT_FILES.length - 1;
      xtRenderAll();
    }));
  }
  function xtRenderToolbarState() {
    const has = XT_ACTIVE >= 0;
    const f = xtActiveFile();
    ["xtVlookupBtn", "xtStatBtn", "xtDedupeBtn", "xtSortBtn", "xtFilterBtn", "xtReplaceBtn", "xtCalcColBtn", "xtAddRowBtn", "xtDelRowBtn", "xtDelColBtn", "xtDownloadBtn"]
      .forEach(id => { const b = document.getElementById(id); if (b) b.disabled = !has; });
    document.getElementById("xtUndoBtn").disabled = !(f && f.history.length);
    document.getElementById("xtHeaderRowInput").disabled = !has;
    document.getElementById("xtHeaderRowInput").value = f ? f.headerRow : 1;
    const fbtn = document.getElementById("xtFilterBtn");
    if (fbtn) fbtn.textContent = (f && f.filter) ? "🔍 필터 해제" : "🔍 필터";
  }
  function xtRowMatchesFilter(f, row) {
    if (!f.filter) return true;
    return String((row && row[f.filter.col]) ?? "").toLowerCase().includes(f.filter.text.toLowerCase());
  }
  function xtRenderGrid() {
    // 실행 취소 가능 여부 등 툴바 버튼 상태는 그리드가 바뀔 때마다 항상 같이 맞춰 줍니다.
    // (VLOOKUP 등 개별 작업에서 일일이 챙기지 않아도 되도록 여기서 한 번에 처리)
    xtRenderToolbarState();
    const f = xtActiveFile();
    const empty = document.getElementById("xtEmpty");
    const scroll = document.getElementById("xtTableScroll");
    if (!f) { empty.style.display = ""; scroll.style.display = "none"; return; }
    empty.style.display = "none"; scroll.style.display = "";

    const nCols = xtColCount(f);
    let thead = `<tr><th class="xt-corner"><input type="checkbox" id="xtSelAllRows" title="전체 행 선택"></th>`;
    for (let c = 0; c < nCols; c++) {
      thead += `<th data-col="${c}" class="${f.colSel.has(c) ? "xt-colsel" : ""}" title="눌러서 이 열 선택/해제">${esc(xtColLetter(c))}</th>`;
    }
    thead += `</tr>`;

    let tbody = "";
    f.aoa.forEach((row, r) => {
      if (!xtRowMatchesFilter(f, row || [])) return;   // 필터에 안 걸리면 아예 안 그림(숨김)
      const isHeaderRow = (r + 1) === f.headerRow;
      tbody += `<tr class="${isHeaderRow ? "xt-headerrow" : ""}" data-row="${r}">`;
      tbody += `<td class="xt-rownum ${f.rowSel.has(r) ? "xt-rowsel" : ""}"><input type="checkbox" class="xt-rowchk" data-row="${r}" ${f.rowSel.has(r) ? "checked" : ""}>${r + 1}</td>`;
      for (let c = 0; c < nCols; c++) {
        const v = row && row[c] != null ? row[c] : "";
        const noMatch = f.noMatchCells && f.noMatchCells.has(r + "," + c);
        tbody += `<td class="xt-cell${noMatch ? " xt-nomatch" : ""}" contenteditable="true" data-row="${r}" data-col="${c}">${esc(String(v))}</td>`;
      }
      tbody += `</tr>`;
    });

    document.getElementById("xtTable").innerHTML = `<thead>${thead}</thead><tbody>${tbody}</tbody>`;

    document.querySelectorAll("#xtTable th[data-col]").forEach(th => th.addEventListener("click", () => {
      const c = Number(th.dataset.col);
      if (f.colSel.has(c)) f.colSel.delete(c); else f.colSel.add(c);
      xtRenderGrid();
    }));
    document.querySelectorAll(".xt-rowchk").forEach(cb => cb.addEventListener("change", () => {
      const r = Number(cb.dataset.row);
      if (cb.checked) f.rowSel.add(r); else f.rowSel.delete(r);
      cb.closest("td").classList.toggle("xt-rowsel", cb.checked);
    }));
    const selAll = document.getElementById("xtSelAllRows");
    if (selAll) selAll.addEventListener("change", (e) => {
      if (e.target.checked) f.aoa.forEach((_, r) => f.rowSel.add(r)); else f.rowSel.clear();
      xtRenderGrid();
    });
    document.querySelectorAll("#xtTable td.xt-cell").forEach(td => td.addEventListener("blur", () => {
      const r = Number(td.dataset.row), c = Number(td.dataset.col);
      if (!f.aoa[r]) f.aoa[r] = [];
      f.aoa[r][c] = td.textContent;
    }));
  }

  /** 작업 배너의 작은 확인창. collect(overlay)가 false를 돌려주면 닫지 않고 그대로 둡니다(검증 실패).
   *  afterRender(overlay)는 본문을 그린 직후 한 번 불러서, 필드끼리 서로 반응하게 만들 때 씁니다. */
  function xtModal(title, bodyHtml, collect, afterRender) {
    return new Promise((resolve) => {
      let ov = document.getElementById("xtModalOverlay");
      if (!ov) {
        ov = document.createElement("div");
        ov.id = "xtModalOverlay";
        ov.style.cssText = "position:fixed;inset:0;background:rgba(0,0,0,.45);display:flex;align-items:flex-start;justify-content:center;z-index:9999;padding:24px 14px;overflow:auto;";
        document.body.appendChild(ov);
      }
      ov.innerHTML = `<div style="background:var(--surface);border:1px solid var(--border);border-radius:14px;max-width:520px;width:100%;padding:20px;box-shadow:0 16px 50px rgba(0,0,0,.28);margin:auto;">
        <div style="font-size:16px;font-weight:700;margin-bottom:14px;color:var(--text);">${esc(title)}</div>
        <div id="xtModalBody">${bodyHtml}</div>
        <div style="display:flex;gap:8px;justify-content:flex-end;margin-top:16px;">
          <button type="button" class="btn ghost" id="xtModalCancel">취소</button>
          <button type="button" class="btn primary" id="xtModalOk">확인</button>
        </div>
      </div>`;
      ov.style.display = "flex";
      // 배경(어두운 곳)을 눌러도 닫히지 않습니다 — 입력하던 내용이 실수로 다 날아가는 걸 막기 위해서예요.
      // 닫으려면 반드시 "취소" 버튼을 눌러야 해요.
      const finish = (result) => { ov.style.display = "none"; resolve(result); };
      document.getElementById("xtModalCancel").onclick = () => finish(null);
      document.getElementById("xtModalOk").onclick = () => {
        const data = collect ? collect(ov) : "ok";
        if (data === false) return;
        finish(data);
      };
      if (afterRender) afterRender(ov);
    });
  }

  document.getElementById("xtUploadBtn").addEventListener("click", () => document.getElementById("xtFileInput").click());
  document.getElementById("xtFileInput").addEventListener("change", async (e) => {
    if (e.target.files && e.target.files.length) await xtAddFiles(e.target.files);
    e.target.value = "";
  });
  ["dragover", "drop"].forEach(evt => document.getElementById("xtGridWrap").addEventListener(evt, (e) => e.preventDefault()));
  document.getElementById("xtGridWrap").addEventListener("drop", async (e) => {
    if (e.dataTransfer && e.dataTransfer.files && e.dataTransfer.files.length) await xtAddFiles(e.dataTransfer.files);
  });
  document.getElementById("xtUndoBtn").addEventListener("click", xtUndo);
  document.getElementById("xtHeaderRowInput").addEventListener("change", (e) => {
    const f = xtActiveFile(); if (!f) return;
    f.headerRow = Math.max(1, Number(e.target.value) || 1);
    xtRenderGrid();
  });
  document.getElementById("xtDownloadBtn").addEventListener("click", () => {
    const f = xtActiveFile(); if (!f) return;
    const ws = XLSX.utils.aoa_to_sheet(f.aoa);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "작업본");
    XLSX.writeFile(wb, f.name.replace(/\.[^.]+$/, "") + "_작업본.xlsx");
  });
  document.getElementById("xtAddRowBtn").addEventListener("click", () => {
    const f = xtActiveFile(); if (!f) return;
    xtPushHistory();
    const blank = new Array(xtColCount(f)).fill("");
    if (f.rowSel.size) {
      // 체크한 행이 있으면 그 중 가장 아래 행 바로 다음에 끼워 넣습니다(중간 삽입).
      const lastSel = Math.max(...f.rowSel);
      f.aoa.splice(lastSel + 1, 0, blank);
      f.rowSel.clear();
      xtRenderGrid();
      xtSetStatus(`${lastSel + 2}행 위치에 빈 행을 끼워 넣었어요.`);
    } else {
      f.aoa.push(blank);
      xtRenderGrid();
      xtSetStatus("맨 아래에 빈 행을 추가했어요. (중간에 넣으려면 그 위 행을 체크하고 눌러주세요)");
    }
  });
  document.getElementById("xtDelRowBtn").addEventListener("click", () => {
    const f = xtActiveFile(); if (!f) return;
    if (!f.rowSel.size) { alert("삭제할 행을 왼쪽 체크박스로 먼저 골라주세요."); return; }
    if (!confirm(`${f.rowSel.size}개 행을 삭제할까요?`)) return;
    xtPushHistory();
    f.aoa = f.aoa.filter((_, r) => !f.rowSel.has(r));
    f.rowSel.clear();
    xtRenderGrid();
    xtSetStatus("선택한 행을 삭제했어요.");
  });
  document.getElementById("xtDelColBtn").addEventListener("click", () => {
    const f = xtActiveFile(); if (!f) return;
    if (!f.colSel.size) { alert("삭제할 열을 위쪽 열 문자(A, B, C…)를 클릭해서 먼저 골라주세요."); return; }
    const letters = [...f.colSel].sort((a, b) => a - b).map(xtColLetter).join(", ");
    if (!confirm(`${letters} 열을 삭제할까요?`)) return;
    xtPushHistory();
    const cols = [...f.colSel].sort((a, b) => b - a);   // 뒤(오른쪽) 열부터 지워야 인덱스가 안 밀려요
    f.aoa.forEach(row => { if (row) cols.forEach(c => row.splice(c, 1)); });
    f.colSel.clear();
    xtRenderGrid();
    xtSetStatus(`${letters} 열을 삭제했어요.`);
  });

  document.getElementById("xtVlookupBtn").addEventListener("click", async () => {
    const f = xtActiveFile(); if (!f) return;
    if (XT_FILES.length < 2) { alert("매칭할 다른 파일을 먼저 하나 더 올려주세요 (조회할 파일)."); return; }
    const others = XT_FILES.map((x, i) => i).filter(i => i !== XT_ACTIVE);
    const fileOpts = others.map(i => `<option value="${i}">${esc(XT_FILES[i].name)}</option>`).join("");
    const body = `<div style="display:grid;gap:12px;">
      <div><label style="font-size:12px;font-weight:600;color:var(--text-muted);">조회할 파일 (여기서 값을 가져와요)</label>
        <select id="xtVFile" class="selectfilter" style="width:100%;">${fileOpts}</select></div>
      <div><label style="font-size:12px;font-weight:600;color:var(--text-muted);">기준 파일(${esc(f.name)})의 매칭 열</label>
        <select id="xtVBaseKey" class="selectfilter" style="width:100%;">${xtColOptsHtml(f)}</select></div>
      <div><label style="font-size:12px;font-weight:600;color:var(--text-muted);">조회 파일의 매칭 열</label>
        <select id="xtVLookKey" class="selectfilter" style="width:100%;"></select></div>
      <div><label style="font-size:12px;font-weight:600;color:var(--text-muted);">가져올 열 (조회 파일 → 새 열 이름) — 열 문자를 직접 안 적어도, 드롭다운에서 골라요</label>
        <div id="xtVPullRows"></div>
        <button type="button" id="xtVAddPull" class="btn ghost small" style="margin-top:4px;">+ 가져올 열 추가</button></div>
      <div style="font-size:12px;color:var(--text-muted);display:flex;gap:10px;align-items:center;">데이터 시작 행 — 기준
        <input type="number" id="xtVBaseStart" value="${f.headerRow + 1}" min="1" style="width:56px;"> / 조회
        <input type="number" id="xtVLookStart" value="1" min="1" style="width:56px;"></div>
      <div id="xtVErr" style="color:var(--danger);font-size:12px;min-height:16px;"></div>
    </div>`;
    const result = await xtModal("VLOOKUP 매칭", body, (ov) => {
      const lookFileIdx = Number(ov.querySelector("#xtVFile").value);
      const baseKey = Number(ov.querySelector("#xtVBaseKey").value);
      const lookKey = Number(ov.querySelector("#xtVLookKey").value);
      const err = ov.querySelector("#xtVErr");
      const rows = [...ov.querySelectorAll(".xt-pullrow")];
      const pullIdx = rows.map(r => Number(r.querySelector(".xtVPullCol").value));
      const newNames = rows.map(r => r.querySelector(".xtVPullName").value.trim());
      if (!rows.length) { err.textContent = "가져올 열을 하나 이상 골라 주세요."; return false; }
      if (newNames.some(n => !n)) { err.textContent = "새 열 이름을 모두 입력해 주세요."; return false; }
      return {
        lookFileIdx, baseKey, lookKey, pullIdx, newNames,
        baseStart: Number(ov.querySelector("#xtVBaseStart").value) || 1,
        lookStart: Number(ov.querySelector("#xtVLookStart").value) || 1,
      };
    }, (ov) => {
      const pullRowHtml = (lf) => `<div class="xt-pullrow" style="display:flex;gap:8px;margin-bottom:6px;align-items:center;">
          <select class="xtVPullCol selectfilter" style="flex:1;min-width:0;">${xtColOptsHtml(lf)}</select>
          <span style="color:var(--text-muted);flex:none;">→</span>
          <input type="text" class="xtVPullName selectfilter" style="flex:1;min-width:0;" placeholder="새 열 이름" data-auto="1">
          <button type="button" class="xtVPullDel" style="flex:none;border:none;background:none;cursor:pointer;color:var(--text-muted);font-size:15px;padding:2px 4px;">✕</button>
        </div>`;
      const curLookFile = () => XT_FILES[Number(ov.querySelector("#xtVFile").value)];
      const wireRow = (rowEl) => {
        const sel = rowEl.querySelector(".xtVPullCol");
        const nameInp = rowEl.querySelector(".xtVPullName");
        const applyAutoName = () => {
          if (nameInp.dataset.auto !== "1") return;
          const lf = curLookFile();
          const hv = lf.aoa[lf.headerRow - 1] ? String(lf.aoa[lf.headerRow - 1][Number(sel.value)] ?? "").trim() : "";
          nameInp.value = hv || xtColLetter(Number(sel.value));
        };
        sel.addEventListener("change", applyAutoName);
        nameInp.addEventListener("input", () => { nameInp.dataset.auto = "0"; });
        rowEl.querySelector(".xtVPullDel").addEventListener("click", () => {
          if (ov.querySelectorAll(".xt-pullrow").length <= 1) return;   // 최소 1줄은 남겨 둠
          rowEl.remove();
        });
        applyAutoName();
      };
      const addPullRow = () => {
        const wrap = ov.querySelector("#xtVPullRows");
        wrap.insertAdjacentHTML("beforeend", pullRowHtml(curLookFile()));
        wireRow(wrap.lastElementChild);
      };
      const fillLookKey = () => {
        const lf = curLookFile();
        ov.querySelector("#xtVLookKey").innerHTML = xtColOptsHtml(lf);
        ov.querySelector("#xtVLookStart").value = lf.headerRow + 1;   // 그 파일의 헤더 행 기준으로 자동 추천
        // 조회할 파일이 바뀌면, 이미 만들어둔 '가져올 열' 줄들도 그 파일 기준으로 다시 채웁니다.
        ov.querySelectorAll(".xt-pullrow").forEach(rowEl => {
          rowEl.querySelector(".xtVPullCol").innerHTML = xtColOptsHtml(lf);
          rowEl.querySelector(".xtVPullName").dataset.auto = "1";
          wireRow(rowEl);
        });
      };
      fillLookKey();
      addPullRow();
      ov.querySelector("#xtVFile").addEventListener("change", fillLookKey);
      ov.querySelector("#xtVAddPull").addEventListener("click", addPullRow);
    });
    if (!result) return;
    xtPushHistory();
    const lookFile = XT_FILES[result.lookFileIdx];
    const map = new Map();
    for (let r = result.lookStart - 1; r < lookFile.aoa.length; r++) {
      const row = lookFile.aoa[r]; if (!row) continue;
      const key = xtNormKey(row[result.lookKey]);
      if (key && !map.has(key)) map.set(key, row);
    }
    const baseColCount = xtColCount(f);
    const targetCols = result.newNames.map((_, k) => baseColCount + k);
    if (!f.aoa[f.headerRow - 1]) f.aoa[f.headerRow - 1] = [];
    targetCols.forEach((tc, k) => { f.aoa[f.headerRow - 1][tc] = result.newNames[k]; });
    f.noMatchCells = new Set();
    let matched = 0, unmatched = 0;
    for (let r = result.baseStart - 1; r < f.aoa.length; r++) {
      if (r === f.headerRow - 1) continue;
      const row = f.aoa[r]; if (!row || !row.some(v => String(v || "").trim())) continue;
      const key = xtNormKey(row[result.baseKey]);
      const hit = key ? map.get(key) : null;
      if (hit) {
        matched++;
        targetCols.forEach((tc, k) => { row[tc] = hit[result.pullIdx[k]] != null ? hit[result.pullIdx[k]] : ""; });
      } else {
        unmatched++;
        targetCols.forEach((tc) => { row[tc] = ""; f.noMatchCells.add(r + "," + tc); });
      }
    }
    xtRenderGrid();
    xtSetStatus(`매칭 완료 — ${matched}건 일치, ${unmatched}건 매칭 안 됨(빨간 칸)`, unmatched ? "warn" : "ok");
  });

  document.getElementById("xtStatBtn").addEventListener("click", async () => {
    const f = xtActiveFile(); if (!f) return;
    const body = `<div style="display:grid;gap:12px;">
      <div><label style="font-size:12px;font-weight:600;color:var(--text-muted);">대상 열</label>
        <select id="xtSCol" class="selectfilter" style="width:100%;">${xtColOptsHtml(f)}</select></div>
      <div><label style="font-size:12px;font-weight:600;color:var(--text-muted);">계산 종류</label>
        <select id="xtSKind" class="selectfilter" style="width:100%;">
          <option value="sum">합계 (SUM)</option>
          <option value="avg">평균 (AVERAGE)</option>
          <option value="count">숫자 개수 (COUNT)</option>
          <option value="counta">채워진 셀 개수 (COUNTA)</option>
        </select></div>
      <div style="font-size:12px;color:var(--text-muted);">데이터 시작 행 <input type="number" id="xtSStart" value="${f.headerRow + 1}" min="1" style="width:56px;"></div>
    </div>`;
    const params = await xtModal("합계·평균·개수", body, (ov) => ({
      col: Number(ov.querySelector("#xtSCol").value),
      kind: ov.querySelector("#xtSKind").value,
      start: Number(ov.querySelector("#xtSStart").value) || 1,
    }));
    if (!params) return;
    const nums = []; let filled = 0;
    for (let r = params.start - 1; r < f.aoa.length; r++) {
      const row = f.aoa[r]; if (!row) continue;
      const raw = row[params.col];
      if (raw !== "" && raw != null) filled++;
      const n = Number(String(raw).replace(/,/g, ""));
      if (String(raw).trim() !== "" && Number.isFinite(n)) nums.push(n);
    }
    let result;
    if (params.kind === "sum") result = nums.reduce((a, b) => a + b, 0);
    else if (params.kind === "avg") result = nums.length ? nums.reduce((a, b) => a + b, 0) / nums.length : 0;
    else if (params.kind === "count") result = nums.length;
    else result = filled;
    xtSetStatus(`${xtColLabel(f, params.col)} 열 결과 — ${result.toLocaleString(undefined, { maximumFractionDigits: 2 })}`, "ok");
  });

  document.getElementById("xtDedupeBtn").addEventListener("click", async () => {
    const f = xtActiveFile(); if (!f) return;
    const body = `<div style="display:grid;gap:12px;">
      <div><label style="font-size:12px;font-weight:600;color:var(--text-muted);">기준 열 (여러 개면 쉼표, 예: A,C)</label>
        <input type="text" id="xtDCols" placeholder="예: A" class="selectfilter" style="width:100%;"></div>
      <div style="font-size:12px;color:var(--text-muted);">데이터 시작 행 <input type="number" id="xtDStart" value="${f.headerRow + 1}" min="1" style="width:56px;"></div>
      <div id="xtDErr" style="color:var(--danger);font-size:12px;min-height:16px;"></div>
    </div>`;
    const params = await xtModal("중복 제거", body, (ov) => {
      const cols = ov.querySelector("#xtDCols").value.split(",").map(s => s.trim()).filter(Boolean).map(xtLetterToIdx);
      const err = ov.querySelector("#xtDErr");
      if (!cols.length || cols.some(c => c == null)) { err.textContent = "기준 열을 A, B 형식으로 입력해 주세요."; return false; }
      return { cols, start: Number(ov.querySelector("#xtDStart").value) || 1 };
    });
    if (!params) return;
    xtPushHistory();
    const seen = new Set(); const kept = []; let removed = 0;
    f.aoa.forEach((row, r) => {
      if (r < params.start - 1) { kept.push(row); return; }
      const key = params.cols.map(c => xtNormKey(row ? row[c] : "")).join("§");
      if (seen.has(key)) { removed++; return; }
      seen.add(key); kept.push(row);
    });
    f.aoa = kept; f.rowSel.clear();
    xtRenderGrid();
    xtSetStatus(`중복 ${removed}건을 지웠어요.`, "ok");
  });

  document.getElementById("xtSortBtn").addEventListener("click", async () => {
    const f = xtActiveFile(); if (!f) return;
    const body = `<div style="display:grid;gap:12px;">
      <div><label style="font-size:12px;font-weight:600;color:var(--text-muted);">정렬 기준 열</label>
        <select id="xtStCol" class="selectfilter" style="width:100%;">${xtColOptsHtml(f)}</select></div>
      <div><label style="font-size:12px;font-weight:600;color:var(--text-muted);">순서</label>
        <select id="xtStDir" class="selectfilter" style="width:100%;">
          <option value="asc">오름차순 (작은→큰, 가나다)</option>
          <option value="desc">내림차순 (큰→작은)</option>
        </select></div>
      <div style="font-size:12px;color:var(--text-muted);">데이터 시작 행 <input type="number" id="xtStStart" value="${f.headerRow + 1}" min="1" style="width:56px;"></div>
    </div>`;
    const params = await xtModal("정렬", body, (ov) => ({
      col: Number(ov.querySelector("#xtStCol").value),
      dir: ov.querySelector("#xtStDir").value,
      start: Number(ov.querySelector("#xtStStart").value) || 1,
    }));
    if (!params) return;
    xtPushHistory();
    const head = f.aoa.slice(0, params.start - 1);
    const bodyRows = f.aoa.slice(params.start - 1);
    bodyRows.sort((a, b) => {
      const av = a ? a[params.col] : "", bv = b ? b[params.col] : "";
      const an = Number(String(av).replace(/,/g, "")), bn = Number(String(bv).replace(/,/g, ""));
      let cmp;
      if (String(av).trim() !== "" && String(bv).trim() !== "" && Number.isFinite(an) && Number.isFinite(bn)) cmp = an - bn;
      else cmp = String(av).localeCompare(String(bv), "ko");
      return params.dir === "desc" ? -cmp : cmp;
    });
    f.aoa = head.concat(bodyRows); f.rowSel.clear();
    xtRenderGrid();
    xtSetStatus("정렬했어요.", "ok");
  });

  document.getElementById("xtFilterBtn").addEventListener("click", async () => {
    const f = xtActiveFile(); if (!f) return;
    if (f.filter) { f.filter = null; xtRenderGrid(); xtRenderToolbarState(); xtSetStatus("필터를 해제했어요.", "ok"); return; }
    const body = `<div style="display:grid;gap:12px;">
      <div><label style="font-size:12px;font-weight:600;color:var(--text-muted);">필터 열</label>
        <select id="xtFCol" class="selectfilter" style="width:100%;">${xtColOptsHtml(f)}</select></div>
      <div><label style="font-size:12px;font-weight:600;color:var(--text-muted);">이 텍스트를 포함하는 행만 보기</label>
        <input type="text" id="xtFText" class="selectfilter" style="width:100%;" placeholder="예: 완료"></div>
    </div>`;
    const params = await xtModal("필터 (버튼을 다시 누르면 해제돼요)", body, (ov) => ({
      col: Number(ov.querySelector("#xtFCol").value),
      text: ov.querySelector("#xtFText").value,
    }));
    if (!params || !params.text) return;
    f.filter = params;
    xtRenderGrid(); xtRenderToolbarState();
    xtSetStatus(`"${params.text}" 포함된 행만 보여요. (필터 버튼을 다시 누르면 해제)`, "ok");
  });

  document.getElementById("xtReplaceBtn").addEventListener("click", async () => {
    const f = xtActiveFile(); if (!f) return;
    const body = `<div style="display:grid;gap:12px;">
      <div><label style="font-size:12px;font-weight:600;color:var(--text-muted);">적용 범위</label>
        <select id="xtRCol" class="selectfilter" style="width:100%;"><option value="">전체 열</option>${xtColOptsHtml(f)}</select></div>
      <div><label style="font-size:12px;font-weight:600;color:var(--text-muted);">찾을 텍스트</label>
        <input type="text" id="xtRFind" class="selectfilter" style="width:100%;"></div>
      <div><label style="font-size:12px;font-weight:600;color:var(--text-muted);">바꿀 텍스트</label>
        <input type="text" id="xtRTo" class="selectfilter" style="width:100%;"></div>
      <div id="xtRErr" style="color:var(--danger);font-size:12px;min-height:16px;"></div>
    </div>`;
    const params = await xtModal("찾기·바꾸기", body, (ov) => {
      const find = ov.querySelector("#xtRFind").value;
      if (!find) { ov.querySelector("#xtRErr").textContent = "찾을 텍스트를 입력해 주세요."; return false; }
      const colVal = ov.querySelector("#xtRCol").value;
      return { col: colVal === "" ? null : Number(colVal), find, to: ov.querySelector("#xtRTo").value };
    });
    if (!params) return;
    xtPushHistory();
    let count = 0;
    f.aoa.forEach(row => {
      if (!row) return;
      row.forEach((v, c) => {
        if (params.col != null && c !== params.col) return;
        const s = String(v == null ? "" : v);
        if (s.includes(params.find)) { row[c] = s.split(params.find).join(params.to); count++; }
      });
    });
    xtRenderGrid();
    xtSetStatus(`${count}칸을 바꿨어요.`, "ok");
  });

  document.getElementById("xtCalcColBtn").addEventListener("click", async () => {
    const f = xtActiveFile(); if (!f) return;
    const body = `<div style="display:grid;gap:12px;">
      <div><label style="font-size:12px;font-weight:600;color:var(--text-muted);">새 열 이름</label>
        <input type="text" id="xtCName" class="selectfilter" style="width:100%;" placeholder="예: 합계"></div>
      <div><label style="font-size:12px;font-weight:600;color:var(--text-muted);">계산 방식</label>
        <select id="xtCKind" class="selectfilter" style="width:100%;">
          <option value="add">더하기 (A+B)</option>
          <option value="sub">빼기 (A−B)</option>
          <option value="mul">곱하기 (A×B)</option>
          <option value="div">나누기 (A÷B)</option>
          <option value="concat">텍스트 합치기 (A+구분자+B)</option>
          <option value="if">조건 (IF) — 조건에 따라 값 넣기</option>
        </select></div>
      <div id="xtCAbWrap" style="display:flex;gap:8px;">
        <div style="flex:1;"><label style="font-size:12px;font-weight:600;color:var(--text-muted);">열 A</label>
          <select id="xtCColA" class="selectfilter" style="width:100%;">${xtColOptsHtml(f)}</select></div>
        <div style="flex:1;"><label style="font-size:12px;font-weight:600;color:var(--text-muted);">열 B</label>
          <select id="xtCColB" class="selectfilter" style="width:100%;">${xtColOptsHtml(f)}</select></div>
      </div>
      <div id="xtCSepWrap" style="display:none;"><label style="font-size:12px;font-weight:600;color:var(--text-muted);">구분자</label>
        <input type="text" id="xtCSep" class="selectfilter" style="width:100%;" placeholder="예: 공백 한 칸, - 등"></div>
      <div id="xtCIfWrap" style="display:none;">
        <div style="display:flex;gap:8px;">
          <div style="flex:1;"><label style="font-size:12px;font-weight:600;color:var(--text-muted);">조건 열</label>
            <select id="xtCIfCol" class="selectfilter" style="width:100%;">${xtColOptsHtml(f)}</select></div>
          <div style="flex:1;"><label style="font-size:12px;font-weight:600;color:var(--text-muted);">조건</label>
            <select id="xtCIfOp" class="selectfilter" style="width:100%;">
              <option value="eq">같다 (=)</option>
              <option value="ne">다르다 (≠)</option>
              <option value="gt">보다 크다 (&gt;)</option>
              <option value="lt">보다 작다 (&lt;)</option>
              <option value="gte">크거나 같다 (≥)</option>
              <option value="lte">작거나 같다 (≤)</option>
              <option value="contains">포함한다</option>
              <option value="empty">비어있다</option>
              <option value="notempty">비어있지 않다</option>
            </select></div>
        </div>
        <div id="xtCIfValWrap" style="margin-top:10px;"><label style="font-size:12px;font-weight:600;color:var(--text-muted);">비교값</label>
          <input type="text" id="xtCIfVal" class="selectfilter" style="width:100%;" placeholder="예: 완료"></div>
        <div style="display:flex;gap:8px;margin-top:10px;">
          <div style="flex:1;"><label style="font-size:12px;font-weight:600;color:var(--text-muted);">참일 때 값</label>
            <input type="text" id="xtCIfTrue" class="selectfilter" style="width:100%;"></div>
          <div style="flex:1;"><label style="font-size:12px;font-weight:600;color:var(--text-muted);">거짓일 때 값</label>
            <input type="text" id="xtCIfFalse" class="selectfilter" style="width:100%;"></div>
        </div>
      </div>
      <div style="font-size:12px;color:var(--text-muted);">데이터 시작 행 <input type="number" id="xtCStart" value="${f.headerRow + 1}" min="1" style="width:56px;"></div>
      <div id="xtCErr" style="color:var(--danger);font-size:12px;min-height:16px;"></div>
    </div>`;
    const params = await xtModal("계산열 추가", body, (ov) => {
      const name = ov.querySelector("#xtCName").value.trim();
      if (!name) { ov.querySelector("#xtCErr").textContent = "새 열 이름을 입력해 주세요."; return false; }
      const kind = ov.querySelector("#xtCKind").value;
      const base = { name, kind, start: Number(ov.querySelector("#xtCStart").value) || 1 };
      if (kind === "if") {
        return Object.assign(base, {
          ifCol: Number(ov.querySelector("#xtCIfCol").value),
          ifOp: ov.querySelector("#xtCIfOp").value,
          ifVal: ov.querySelector("#xtCIfVal").value,
          ifTrue: ov.querySelector("#xtCIfTrue").value,
          ifFalse: ov.querySelector("#xtCIfFalse").value,
        });
      }
      return Object.assign(base, {
        colA: Number(ov.querySelector("#xtCColA").value), colB: Number(ov.querySelector("#xtCColB").value),
        sep: ov.querySelector("#xtCSep").value,
      });
    }, (ov) => {
      const syncKindUi = () => {
        const kind = ov.querySelector("#xtCKind").value;
        ov.querySelector("#xtCAbWrap").style.display = kind === "if" ? "none" : "flex";
        ov.querySelector("#xtCSepWrap").style.display = kind === "concat" ? "" : "none";
        ov.querySelector("#xtCIfWrap").style.display = kind === "if" ? "" : "none";
      };
      const syncIfValUi = () => {
        const op = ov.querySelector("#xtCIfOp").value;
        ov.querySelector("#xtCIfValWrap").style.display = (op === "empty" || op === "notempty") ? "none" : "";
      };
      ov.querySelector("#xtCKind").addEventListener("change", syncKindUi);
      ov.querySelector("#xtCIfOp").addEventListener("change", syncIfValUi);
      syncKindUi(); syncIfValUi();
    });
    if (!params) return;
    xtPushHistory();
    const newCol = xtColCount(f);
    if (!f.aoa[f.headerRow - 1]) f.aoa[f.headerRow - 1] = [];
    f.aoa[f.headerRow - 1][newCol] = params.name;
    for (let r = params.start - 1; r < f.aoa.length; r++) {
      if (r === f.headerRow - 1) continue;
      const row = f.aoa[r]; if (!row || !row.some(v => String(v || "").trim())) continue;
      let val;
      if (params.kind === "if") {
        const cell = row[params.ifCol];
        const cellStr = String(cell ?? "").trim();
        const cn = Number(String(cell).replace(/,/g, "")), vn = Number(String(params.ifVal).replace(/,/g, ""));
        const numOk = Number.isFinite(cn) && Number.isFinite(vn) && cellStr !== "" && String(params.ifVal).trim() !== "";
        let cond;
        if (params.ifOp === "empty") cond = cellStr === "";
        else if (params.ifOp === "notempty") cond = cellStr !== "";
        else if (params.ifOp === "contains") cond = cellStr.includes(params.ifVal);
        else if (params.ifOp === "eq") cond = numOk ? cn === vn : xtNormKey(cell) === xtNormKey(params.ifVal);
        else if (params.ifOp === "ne") cond = numOk ? cn !== vn : xtNormKey(cell) !== xtNormKey(params.ifVal);
        else if (params.ifOp === "gt") cond = numOk && cn > vn;
        else if (params.ifOp === "lt") cond = numOk && cn < vn;
        else if (params.ifOp === "gte") cond = numOk && cn >= vn;
        else cond = numOk && cn <= vn;
        val = cond ? params.ifTrue : params.ifFalse;
      } else {
        const a = row[params.colA], b = row[params.colB];
        const an = Number(String(a).replace(/,/g, "")), bn = Number(String(b).replace(/,/g, ""));
        if (params.kind === "concat") val = String(a ?? "") + (params.sep || "") + String(b ?? "");
        else if (!Number.isFinite(an) || !Number.isFinite(bn)) val = "";
        else if (params.kind === "add") val = an + bn;
        else if (params.kind === "sub") val = an - bn;
        else if (params.kind === "mul") val = an * bn;
        else val = bn !== 0 ? an / bn : "";
      }
      row[newCol] = val;
    }
    xtRenderGrid();
    xtSetStatus(`"${params.name}" 열을 추가했어요.`, "ok");
  });

  // Slack 링크 버튼(#form)으로 들어온 경우, 요청서 작성 화면으로 바로 이동합니다.
  // 처리한 뒤엔 주소의 #form을 지워서, 나중에 새로고침(F5)해도 계속 요청서 작성으로
  // 튕기지 않고 원래 화면(대시보드)으로 뜨게 합니다.
  if (window.location.hash === "#form") {
    setView("form");
    history.replaceState(null, "", window.location.pathname + window.location.search);
  }

  // 안전장치: 화면이 다시 그려지는 타이밍과 상관없이, 3초마다 강제로 배지를 다시 칠합니다.
  setInterval(() => {
    document.querySelectorAll("[data-stock-code]").forEach(el => {
      const code = el.getAttribute("data-stock-code");
      if (!code) return;
      if (el.hasAttribute("data-req-qty")) { paintLineStock(el); return; }
      const s = STOCK[code];
      if (s) {
        el.textContent = s.available != null ? `가용 ${s.available}` : `재고 ${s.stock}`;
      }
    });
  }, 3000);

  (function(){
    const root = document.documentElement;
    const btn = document.getElementById("themeToggle");
    if (!btn) return;
    let mode = null; // null = follow OS
    btn.addEventListener("click", () => {
      const osDark = window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches;
      const cur = mode || (osDark ? "dark" : "light");
      mode = cur === "dark" ? "light" : "dark";
      root.setAttribute("data-theme", mode);
    });
  })();
</script>
<script>
  // PWA: 서비스워커 등록 (홈 화면 설치형 앱 + 오프라인 최소 지원)
  /* =====================================================================
     서비스워커 등록 + 새 버전 자동 적용
     배포했는데 화면이 안 바뀌던 문제(강력 새로고침 필요)를 없앱니다.
      - sw.js를 캐시 없이(updateViaCache:'none') 확인
      - 탭을 다시 켤 때마다 새 버전이 있는지 조용히 확인
      - 새 버전이 준비되면: 요청서 작성 중이 아니면 자동 새로고침,
        입력 중이면 화면을 날리지 않고 오른쪽 아래 '새로고침' 안내만 띄웁니다.
     ===================================================================== */
  if ('serviceWorker' in navigator) {
    window.addEventListener('load', function () {
      var hadController = !!navigator.serviceWorker.controller;   // 이미 켜져 있던 사용자인지
      var reloading = false;

      // 지금 새로고침해도 안전한가? (요청서 입력 중이면 안전하지 않음)
      function safeToReload() {
        try {
          var form = document.getElementById("formSection");
          if (form && form.style.display !== "none") {
            var dirty = [...form.querySelectorAll("input[type=text], input[type=tel], input[type=number], input[type=date], textarea")]
              .some(function (el) { return el.value && String(el.value).trim(); });
            if (dirty) return false;
          }
        } catch (e) {}
        return true;
      }

      function showUpdateBar() {
        if (document.getElementById("swUpdateBar")) return;
        var bar = document.createElement("div");
        bar.id = "swUpdateBar";
        bar.style.cssText = "position:fixed;right:18px;bottom:18px;z-index:10000;display:flex;align-items:center;gap:10px;"
          + "padding:12px 14px;border-radius:12px;background:var(--surface,#fff);border:1px solid var(--border,#dde1e7);"
          + "box-shadow:0 10px 30px rgba(0,0,0,.18);font-size:13px;color:var(--text,#22252a);max-width:320px;";
        bar.innerHTML = '<span style="line-height:1.5;">새 버전이 나왔어요. 작성 중인 내용을 저장한 뒤 새로고침해 주세요.</span>'
          + '<button type="button" id="swReloadBtn" style="flex:none;appearance:none;cursor:pointer;font-family:inherit;font-size:13px;font-weight:700;'
          + 'padding:8px 12px;border-radius:8px;border:1px solid var(--accent,#2f6fed);background:var(--accent,#2f6fed);color:#fff;">새로고침</button>';
        document.body.appendChild(bar);
        document.getElementById("swReloadBtn").addEventListener("click", function () { location.reload(); });
      }

      navigator.serviceWorker.register('sw.js', { updateViaCache: 'none' }).then(function (reg) {
        reg.update().catch(function () {});
        // 새 서비스워커가 설치되면 바로 활성화시킵니다.
        reg.addEventListener('updatefound', function () {
          var nw = reg.installing;
          if (!nw) return;
          nw.addEventListener('statechange', function () {
            if (nw.state === 'installed' && navigator.serviceWorker.controller) {
              nw.postMessage({ type: 'SKIP_WAITING' });
            }
          });
        });
        // 탭을 다시 열 때 조용히 새 버전 확인
        document.addEventListener('visibilitychange', function () {
          if (document.visibilityState === 'visible') reg.update().catch(function () {});
        });
      }).catch(function () {});

      // 새 버전이 화면을 넘겨받는 순간 (= 배포된 최신본이 준비된 순간)
      navigator.serviceWorker.addEventListener('controllerchange', function () {
        if (!hadController || reloading) return;   // 첫 방문에는 새로고침하지 않음
        if (safeToReload()) { reloading = true; location.reload(); }
        else showUpdateBar();
      });
    });
  }

  // 모바일에서는 '실시간 재고 검색' 패널을 상품정보 바로 위로 옮깁니다.
  // (데스크톱에서는 원래대로 폼 오른쪽 사이드바로 둡니다.)
  (function(){
    var mq = window.matchMedia('(max-width: 820px)');
    var panel = document.querySelector('.stockpanel');
    var formpage = document.getElementById('formSection');
    var prows = document.getElementById('productRows');
    var prodCard = prows ? prows.closest('.fcard') : null;
    if (!panel || !formpage || !prodCard) return;
    function place(){
      if (mq.matches){
        // 모바일: 상품정보 카드 바로 앞에 삽입
        if (prodCard.previousElementSibling !== panel){
          prodCard.parentNode.insertBefore(panel, prodCard);
        }
      } else {
        // 데스크톱: 폼 옆(사이드바) — formpage의 마지막으로 복귀
        if (formpage.lastElementChild !== panel){
          formpage.appendChild(panel);
        }
      }
    }
    place();
    if (mq.addEventListener) mq.addEventListener('change', place);
    else if (mq.addListener) mq.addListener(place);
  })();
</script>
