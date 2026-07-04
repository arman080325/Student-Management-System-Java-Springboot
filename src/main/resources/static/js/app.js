/* ============================================================
   Student Management System — client logic
   Talks to the Spring Boot REST API at /api/students
   ============================================================ */

const API = "/api/students";

const COURSES  = ["B.Tech", "BBA", "BCA", "BSC", "MSC", "MBA", "MCA", "MCom", "MA", "BA"];
const BRANCHES = [
  "Computer Science & Engineering (CSE)",
  "Computer Science & Technology (CST)",
  "Electronics & Communication Engineering (ECE)",
  "Electrical & Electronics Engineering (EEE)",
  "Electronics & Instrumentation Engineering (EIE)",
];

const AVATAR_PALETTE = ["#006c47", "#0d6b8f", "#7a4fc9", "#b8722f", "#2f7ab8", "#8f5d0d", "#3f8f5d"];

const $  = (s, r = document) => r.querySelector(s);
const $$ = (s, r = document) => [...r.querySelectorAll(s)];

const els = {
  body:        $("#tableBody"),
  count:       $("#countChip"),
  statusCount: $("#statusCount"),
  search:      $("#searchInput"),
  field:       $("#fieldSelect"),
  pageSize:    $("#pageSizeSelect"),
  form:        $("#studentForm"),
  formOverlay: $("#formOverlay"),
  formTitle:   $("#formTitle"),
  rollField:   $("#rollField"),
  rollDisplay: $("#rollDisplay"),
  confirmOverlay: $("#confirmOverlay"),
  confirmTitle:   $("#confirmTitle"),
  confirmMsg:     $("#confirmMsg"),
  helpOverlay: $("#helpOverlay"),
  toasts:      $("#toasts"),
  selectAll:   $("#selectAllCheckbox"),
  bulkBar:     $("#bulkBar"),
  bulkCount:   $("#bulkCount"),
  paginationInfo: $("#paginationInfo"),
  pageIndicator:  $("#pageIndicator"),
};

let editingId = null;
let confirmAction = null;
let currentPage = 0;
let currentSize = 10;
let sortBy = "createdAt";
let sortDir = "desc";
let lastPageMeta = { totalPages: 1, totalElements: 0, first: true, last: true };
let selectedIds = new Set();
let pendingGKey = false;

/* ---------- helpers ---------- */
const esc = (v) =>
  v == null ? "" : String(v).replace(/[&<>"']/g, (c) =>
    ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));

const dash = (v) => (v == null || v === "" ? '<span class="cell-dim">—</span>' : esc(v));

function initials(name) {
  if (!name) return "?";
  const parts = name.trim().split(/\s+/);
  return ((parts[0]?.[0] || "") + (parts[1]?.[0] || "")).toUpperCase();
}

function avatarColor(name) {
  let hash = 0;
  for (const ch of String(name || "")) hash = (hash * 31 + ch.charCodeAt(0)) >>> 0;
  return AVATAR_PALETTE[hash % AVATAR_PALETTE.length];
}

function pctBadge(pct) {
  if (pct == null) return '<span class="cell-dim">—</span>';
  const cls = pct >= 85 ? "pct-good" : pct >= 60 ? "pct-mid" : "pct-low";
  return `<span class="pct-badge ${cls}">${esc(pct)}%</span>`;
}

function toast(msg, type = "ok") {
  const t = document.createElement("div");
  t.className = "toast" + (type === "error" ? " toast--error" : "");
  t.innerHTML = `<span class="toast__prefix">${type === "error" ? "✕" : "✓"}</span>${esc(msg)}`;
  els.toasts.appendChild(t);
  setTimeout(() => {
    t.style.opacity = "0";
    t.style.transform = "translateX(16px)";
    t.style.transition = "all .2s";
    setTimeout(() => t.remove(), 200);
  }, 2800);
}

function isTypingTarget(el) {
  return el && (el.tagName === "INPUT" || el.tagName === "SELECT" || el.tagName === "TEXTAREA" || el.isContentEditable);
}

function anyOverlayOpen() {
  return $$(".overlay.open").length > 0;
}

/* ---------- data ---------- */
async function fetchStudents() {
  const q = els.search.value.trim();
  const params = new URLSearchParams({
    page: String(currentPage),
    size: String(currentSize),
    sortBy,
    sortDir,
  });
  if (q) {
    params.set("field", els.field.value);
    params.set("search", q);
  }
  const res = await fetch(`${API}?${params.toString()}`);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return res.json();
}

function skeleton() {
  els.body.innerHTML = Array.from({ length: 4 }).map(() =>
    `<tr class="skeleton-row">${'<td><div class="skeleton"></div></td>'.repeat(12)}</tr>`).join("");
}

function updateSortIndicators() {
  $$("thead th[data-sort]").forEach((th) => {
    if (th.dataset.sort === sortBy) {
      th.setAttribute("aria-sort", sortDir === "asc" ? "ascending" : "descending");
    } else {
      th.removeAttribute("aria-sort");
    }
  });
}

function updateBulkBar() {
  const n = selectedIds.size;
  els.bulkBar.hidden = n === 0;
  els.bulkCount.textContent = String(n);
  els.selectAll.checked = n > 0 && n === $$(".row-check").length && $$(".row-check").length > 0;
  els.selectAll.indeterminate = n > 0 && !els.selectAll.checked;
}

function render(pageData) {
  const list = pageData.content;
  const n = pageData.totalElements;

  els.count.innerHTML = `<b>${n}</b> record${n === 1 ? "" : "s"}`;
  els.statusCount.textContent = `${n} record${n === 1 ? "" : "s"}`;
  lastPageMeta = pageData;
  selectedIds.clear();

  if (!list.length) {
    const searching = els.search.value.trim().length > 0;
    els.body.innerHTML = `<tr><td colspan="12"><div class="state">
      <div class="state__mono">${searching ? "No matches" : "Empty"}</div>
      <div class="state__title">${searching ? "No records found" : "No students yet"}</div>
      <div class="state__hint">${searching ? "Try a different search term or reset the filter." : "Add your first student to get started."}</div>
    </div></td></tr>`;
  } else {
    els.body.innerHTML = list.map((s) => `
      <tr data-row="${s.id}">
        <td class="col-check"><input type="checkbox" class="row-check" data-id="${s.id}" aria-label="Select ${esc(s.name)}" /></td>
        <td class="cell-roll">${esc(s.rollNo)}</td>
        <td><div class="name-cell"><span class="avatar" style="background:${avatarColor(s.name)}">${initials(s.name)}</span>${esc(s.name)}</div></td>
        <td class="cell-dim">${dash(s.fatherName)}</td>
        <td class="cell-mono cell-dim">${dash(s.dob)}</td>
        <td class="cell-mono cell-dim">${dash(s.phone)}</td>
        <td class="cell-mono cell-dim">${dash(s.email)}</td>
        <td class="cell-mono cell-dim">${dash(s.sic)}</td>
        <td>${pctBadge(s.classXiiPercent)}</td>
        <td><span class="tag">${dash(s.course)}</span></td>
        <td class="cell-dim">${dash(s.branch)}</td>
        <td class="actions-col"><div class="row-actions">
          <button class="icon-btn" data-edit="${s.id}" aria-label="Edit ${esc(s.name)}">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4Z"/></svg>
          </button>
          <button class="icon-btn icon-btn--danger" data-del="${s.id}" data-roll="${esc(s.rollNo)}" aria-label="Delete ${esc(s.name)}">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2m3 0v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6"/></svg>
          </button>
        </div></td>
      </tr>`).join("");
  }

  updateSortIndicators();
  updateBulkBar();

  const startIdx = n === 0 ? 0 : pageData.page * pageData.size + 1;
  const endIdx = Math.min(n, (pageData.page + 1) * pageData.size);
  els.paginationInfo.textContent = `Showing ${startIdx}–${endIdx} of ${n}`;
  els.pageIndicator.textContent = `Page ${pageData.page + 1} / ${Math.max(pageData.totalPages, 1)}`;
  $("#firstPageBtn").disabled = pageData.first;
  $("#prevPageBtn").disabled = pageData.first;
  $("#nextPageBtn").disabled = pageData.last;
  $("#lastPageBtn").disabled = pageData.last;
}

async function reload() {
  try {
    skeleton();
    render(await fetchStudents());
  } catch (e) {
    els.body.innerHTML = `<tr><td colspan="12"><div class="state">
      <div class="state__mono">Connection error</div>
      <div class="state__title">Can't reach the server</div>
      <div class="state__hint">Make sure the app is running, then reset.</div>
    </div></td></tr>`;
    toast("Failed to load records", "error");
  }
}

/* ---------- dashboard ---------- */
function renderBars(container, counts) {
  const entries = Object.entries(counts || {}).sort((a, b) => b[1] - a[1]);
  if (!entries.length) {
    container.innerHTML = `<div class="bars-empty">No data yet</div>`;
    return;
  }
  const max = Math.max(...entries.map(([, c]) => c));
  container.innerHTML = entries.map(([label, count]) => `
    <div class="bar-row">
      <div class="bar-row__top">
        <span class="bar-row__label">${esc(label)}</span>
        <span class="bar-row__count">${count}</span>
      </div>
      <div class="bar-track"><div class="bar-fill" style="width:${Math.round((count / max) * 100)}%"></div></div>
    </div>`).join("");
}

async function loadDashboard() {
  try {
    const stats = await (await fetch(`${API}/stats`)).json();
    $("#statTotal").textContent = stats.totalStudents;
    $("#statAvg").textContent = stats.totalStudents ? `${stats.averageClassXiiPercent}%` : "—";
    $("#statCourses").textContent = stats.totalCourses;
    $("#statBranches").textContent = stats.totalBranches;

    renderBars($("#courseBars"), stats.countByCourse);
    renderBars($("#branchBars"), stats.countByBranch);

    const top = $("#topPerformer");
    if (stats.topPerformer) {
      const p = stats.topPerformer;
      top.innerHTML = `
        <span class="avatar" style="background:${avatarColor(p.name)}">${initials(p.name)}</span>
        <div>
          <div class="top-performer__name">${esc(p.name)}</div>
          <div class="top-performer__meta">${esc(p.rollNo)} · ${esc(p.course)} · ${esc(p.branch)}</div>
        </div>
        <div class="top-performer__pct">${esc(p.classXiiPercent)}%</div>`;
    } else {
      top.innerHTML = `<span class="bars-empty">No records with a Class XII % yet.</span>`;
    }
  } catch {
    toast("Failed to load dashboard stats", "error");
  }
}

/* ---------- views ---------- */
function switchView(view) {
  const isRecords = view === "records";
  $("#recordsView").hidden = !isRecords;
  $("#dashboardView").hidden = isRecords;
  $$(".view-tab").forEach((t) => t.setAttribute("aria-selected", String(t.dataset.view === view)));
  if (!isRecords) loadDashboard();
}

/* ---------- form ---------- */
function fillSelect(name, opts) {
  const sel = els.form.elements[name];
  sel.innerHTML = opts.map((o) => `<option value="${esc(o)}">${esc(o)}</option>`).join("");
}

function clearErrors() {
  $$(".field__error", els.form).forEach((e) => (e.textContent = ""));
  $$("input, select", els.form).forEach((e) => e.classList.remove("invalid"));
}

function openForm(student = null) {
  clearErrors();
  els.form.reset();
  editingId = student ? student.id : null;
  els.formTitle.textContent = student ? "Edit student" : "New student";
  $("#formSubmit").textContent = student ? "Update student" : "Save student";

  if (student) {
    els.rollField.hidden = false;
    els.rollDisplay.textContent = student.rollNo;
    for (const k of ["name", "fatherName", "dob", "phone", "email", "sic", "classXiiPercent", "address", "course", "branch"]) {
      if (els.form.elements[k]) els.form.elements[k].value = student[k] ?? "";
    }
  } else {
    els.rollField.hidden = true;
  }
  els.formOverlay.classList.add("open");
  setTimeout(() => els.form.elements.name.focus(), 60);
}

function closeForm() { els.formOverlay.classList.remove("open"); editingId = null; }

function collect() {
  const d = Object.fromEntries(new FormData(els.form).entries());
  const clean = (v) => (v && v.trim() !== "" ? v.trim() : null);
  return {
    name: clean(d.name),
    fatherName: clean(d.fatherName),
    dob: clean(d.dob),
    address: clean(d.address),
    phone: clean(d.phone),
    email: clean(d.email),
    sic: clean(d.sic),
    classXiiPercent: clean(d.classXiiPercent) == null ? null : Number(d.classXiiPercent),
    course: clean(d.course),
    branch: clean(d.branch),
  };
}

/* client-side mirror of server validation for instant feedback */
function validate(p) {
  const errs = {};
  if (!p.name) errs.name = "Name is required";
  if (!p.fatherName) errs.fatherName = "Father's name is required";
  if (!p.course) errs.course = "Course is required";
  if (!p.branch) errs.branch = "Branch is required";
  if (p.phone && !/^[0-9]{10}$/.test(p.phone)) errs.phone = "Phone must be 10 digits";
  if (p.email && !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(p.email)) errs.email = "Email must be valid";
  if (p.classXiiPercent != null && (isNaN(p.classXiiPercent) || p.classXiiPercent < 0 || p.classXiiPercent > 100))
    errs.classXiiPercent = "Must be between 0 and 100";
  if (p.dob && new Date(p.dob) >= new Date()) errs.dob = "Must be in the past";
  return errs;
}

function showErrors(errs) {
  clearErrors();
  for (const [field, msg] of Object.entries(errs)) {
    const box = $(`[data-error="${field}"]`, els.form);
    const input = els.form.elements[field];
    if (box) box.textContent = msg;
    if (input) input.classList.add("invalid");
  }
}

async function submitForm(e) {
  e.preventDefault();
  const payload = collect();
  const errs = validate(payload);
  if (Object.keys(errs).length) { showErrors(errs); return; }

  const btn = $("#formSubmit");
  btn.disabled = true;
  try {
    const editing = editingId != null;
    const res = await fetch(editing ? `${API}/${editingId}` : API, {
      method: editing ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
    if (res.status === 400) {
      const body = await res.json();
      showErrors(body.fieldErrors || {});
      toast("Check the highlighted fields", "error");
      return;
    }
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    closeForm();
    toast(editing ? "Student updated" : "Student added");
    reload();
  } catch (err) {
    toast("Save failed — is the server running?", "error");
  } finally {
    btn.disabled = false;
  }
}

/* ---------- confirm ---------- */
function askConfirm(title, msg, okLabel, action) {
  els.confirmTitle.textContent = title;
  els.confirmMsg.innerHTML = msg;
  $("#confirmOk").textContent = okLabel;
  confirmAction = action;
  els.confirmOverlay.classList.add("open");
}
function closeConfirm() { els.confirmOverlay.classList.remove("open"); confirmAction = null; }

async function doDelete(id, roll) {
  askConfirm("Delete record", `Delete student <b>${esc(roll)}</b>? This can't be undone.`, "Delete", async () => {
    try {
      const res = await fetch(`${API}/${id}`, { method: "DELETE" });
      if (!res.ok) throw new Error();
      toast("Student deleted");
      reload();
    } catch { toast("Delete failed", "error"); }
  });
}

function doClearAll() {
  askConfirm("Clear all records", "Delete <b>every</b> student record? This can't be undone.", "Clear all", async () => {
    try {
      const res = await fetch(API, { method: "DELETE" });
      if (res.status === 403) { toast("Clear all is disabled in the live demo", "error"); return; }
      if (!res.ok) throw new Error();
      toast("All records cleared");
      currentPage = 0;
      reload();
    } catch { toast("Clear failed", "error"); }
  });
}

function doBulkDelete() {
  const ids = [...selectedIds];
  if (!ids.length) return;
  askConfirm("Delete selected", `Delete <b>${ids.length}</b> selected record${ids.length === 1 ? "" : "s"}? This can't be undone.`, "Delete", async () => {
    try {
      const res = await fetch(`${API}/bulk`, {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(ids),
      });
      if (!res.ok) throw new Error();
      const body = await res.json();
      toast(`${body.deleted} record${body.deleted === 1 ? "" : "s"} deleted`);
      selectedIds.clear();
      reload();
    } catch { toast("Bulk delete failed", "error"); }
  });
}

async function loadMeta() {
  try {
    const m = await (await fetch("/api/meta")).json();
    if (!m.clearAllEnabled) {
      const b = document.querySelector("#clearBtn");
      if (b) b.style.display = "none";
    }
  } catch { /* non-fatal */ }
}

/* ---------- theme ---------- */
const THEME_KEY = "sms-theme";
const themeMq = window.matchMedia("(prefers-color-scheme: dark)");

function getThemeChoice() {
  try { return localStorage.getItem(THEME_KEY) || "dark"; } catch { return "dark"; }
}

function applyTheme(choice) {
  const dark = choice === "dark" || (choice === "system" && themeMq.matches);
  document.documentElement.dataset.theme = dark ? "dark" : "light";
  $$("[data-theme-set]").forEach((b) =>
    b.setAttribute("aria-pressed", String(b.dataset.themeSet === choice)));
}

function setupTheme() {
  applyTheme(getThemeChoice());
  $$("[data-theme-set]").forEach((b) =>
    b.addEventListener("click", () => {
      try { localStorage.setItem(THEME_KEY, b.dataset.themeSet); } catch {}
      applyTheme(b.dataset.themeSet);
    }));
  themeMq.addEventListener("change", () => {
    if (getThemeChoice() === "system") applyTheme("system");
  });
}

/* ---------- events ---------- */
function debounce(fn, ms) { let t; return (...a) => { clearTimeout(t); t = setTimeout(() => fn(...a), ms); }; }

function goToPage(page) {
  const max = Math.max(lastPageMeta.totalPages - 1, 0);
  currentPage = Math.min(Math.max(page, 0), max);
  reload();
}

function init() {
  setupTheme();
  loadMeta();
  fillSelect("course", COURSES);
  fillSelect("branch", BRANCHES);

  $$(".view-tab").forEach((tab) => tab.addEventListener("click", () => switchView(tab.dataset.view)));

  $("#addBtn").addEventListener("click", () => openForm());
  $("#formClose").addEventListener("click", closeForm);
  $("#formCancel").addEventListener("click", closeForm);
  els.form.addEventListener("submit", submitForm);

  $("#resetBtn").addEventListener("click", () => { els.search.value = ""; currentPage = 0; reload(); });
  $("#clearBtn").addEventListener("click", doClearAll);
  $("#exportBtn").addEventListener("click", () => { window.location.href = `${API}/export`; });

  els.search.addEventListener("input", debounce(() => { currentPage = 0; reload(); }, 280));
  els.field.addEventListener("change", () => { if (els.search.value.trim()) { currentPage = 0; reload(); } });
  els.pageSize.addEventListener("change", () => { currentSize = Number(els.pageSize.value); currentPage = 0; reload(); });

  $$("thead th[data-sort]").forEach((th) => th.addEventListener("click", () => {
    const field = th.dataset.sort;
    if (sortBy === field) { sortDir = sortDir === "asc" ? "desc" : "asc"; }
    else { sortBy = field; sortDir = "asc"; }
    currentPage = 0;
    reload();
  }));

  $("#firstPageBtn").addEventListener("click", () => goToPage(0));
  $("#prevPageBtn").addEventListener("click", () => goToPage(currentPage - 1));
  $("#nextPageBtn").addEventListener("click", () => goToPage(currentPage + 1));
  $("#lastPageBtn").addEventListener("click", () => goToPage(lastPageMeta.totalPages - 1));

  els.selectAll.addEventListener("change", () => {
    $$(".row-check").forEach((cb) => {
      cb.checked = els.selectAll.checked;
      const id = Number(cb.dataset.id);
      cb.closest("tr").classList.toggle("is-selected", cb.checked);
      if (cb.checked) selectedIds.add(id); else selectedIds.delete(id);
    });
    updateBulkBar();
  });

  $("#bulkClearBtn").addEventListener("click", () => {
    selectedIds.clear();
    $$(".row-check").forEach((cb) => { cb.checked = false; cb.closest("tr").classList.remove("is-selected"); });
    updateBulkBar();
  });
  $("#bulkDeleteBtn").addEventListener("click", doBulkDelete);

  els.body.addEventListener("change", (e) => {
    const cb = e.target.closest(".row-check");
    if (!cb) return;
    const id = Number(cb.dataset.id);
    if (cb.checked) selectedIds.add(id); else selectedIds.delete(id);
    cb.closest("tr").classList.toggle("is-selected", cb.checked);
    updateBulkBar();
  });

  els.body.addEventListener("click", (e) => {
    const edit = e.target.closest("[data-edit]");
    const del = e.target.closest("[data-del]");
    if (edit) {
      fetch(`${API}/${edit.dataset.edit}`).then((r) => r.json()).then(openForm).catch(() => toast("Couldn't load record", "error"));
    } else if (del) {
      doDelete(del.dataset.del, del.dataset.roll);
    }
  });

  $("#confirmCancel").addEventListener("click", closeConfirm);
  $("#confirmOk").addEventListener("click", async () => { const a = confirmAction; closeConfirm(); if (a) await a(); });
  $("#helpClose").addEventListener("click", () => els.helpOverlay.classList.remove("open"));

  [els.formOverlay, els.confirmOverlay, els.helpOverlay].forEach((o) =>
    o.addEventListener("click", (e) => { if (e.target === o) o.classList.remove("open"); }));

  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") { closeForm(); closeConfirm(); els.helpOverlay.classList.remove("open"); return; }

    if (isTypingTarget(e.target) || anyOverlayOpen()) return;

    if (e.key === "/") { e.preventDefault(); els.search.focus(); return; }
    if (e.key === "n" || e.key === "N") { e.preventDefault(); openForm(); return; }
    if (e.key === "?") { e.preventDefault(); els.helpOverlay.classList.add("open"); return; }
    if (e.key === "g" || e.key === "G") { pendingGKey = true; setTimeout(() => (pendingGKey = false), 800); return; }
    if (pendingGKey && (e.key === "d" || e.key === "D")) { pendingGKey = false; switchView("dashboard"); return; }
    if (pendingGKey && (e.key === "r" || e.key === "R")) { pendingGKey = false; switchView("records"); return; }
  });

  reload();
}

document.addEventListener("DOMContentLoaded", init);
