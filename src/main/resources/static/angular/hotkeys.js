/*!
 * angular-hotkeys v1.4.3
 * https://chieffancypants.github.io/angular-hotkeys
 * Copyright (c) 2014 Wes Cruver
 * License: MIT
 */

!function () {
    "use strict";
    angular.module("cfp.hotkeys", []).provider("hotkeys", function () {
            this.includeCheatSheet = !0, this.templateTitle = "Keyboard Shortcuts:", this.template = '<div class="cfp-hotkeys-container fade" ng-class="{in: helpVisible}" style="display: none;"><div class="cfp-hotkeys"><h4 class="cfp-hotkeys-title">{{ title }}</h4><table><tbody><tr ng-repeat="hotkey in hotkeys | filter:{ description: \'!$$undefined$$\' }"><td class="cfp-hotkeys-keys"><span ng-repeat="key in hotkey.format() track by $index" class="cfp-hotkeys-key">{{ key }}</span></td><td class="cfp-hotkeys-text">{{ hotkey.description }}</td></tr></tbody></table><div class="cfp-hotkeys-close" ng-click="toggleCheatSheet()">×</div></div></div>', this.cheatSheetHotkey = "?", this.cheatSheetDescription = "Show / hide this help menu", this.$get = ["$rootElement", "$rootScope", "$compile", "$window", "$document", function (a, b, c, d, e) {
                function f(a) {
                    var b = {
                            command: "⌘", shift: "⇧", left: "←", right: "→", up: "↑", down: "↓", "return": "↩", backspace: "⌫"
                        }
                    ;
                    a = a.split("+");
                    for (var c = 0;
                         c < a.length;
                         c++) "mod" === a[c] && (a[c] = d.navigator && d.navigator.platform.indexOf("Mac") >= 0 ? "command" : "ctrl"),
                        a[c] = b[a[c]] || a[c];
                    return a.join(" + ")
                }

                function g(a,
                           b,
                           c,
                           d,
                           e,
                           f) {
                    this.combo = a instanceof Array ? a : [a], this.description = b, this.callback = c, this.action = d, this.allowIn = e, this.persistent = f
                }

                function h() {
                    for (var a = o.hotkeys.length;
                         a--;
                    ) {
                        var b = o.hotkeys[a];
                        b && !b.persistent && k(b)
                    }
                }

                function i() {
                    o.helpVisible = !o.helpVisible, o.helpVisible ? (t = l("esc"), k("esc"), j("esc", t.description, i)) : (k("esc"), t !== !1 && j(t))
                }

                function j(a,
                           b,
                           c,
                           d,
                           e,
                           f) {
                    var h, i = ["INPUT", "SELECT", "TEXTAREA"], j = Object.prototype.toString.call(a);
                    if ("[object Object]" === j && (b = a.description, c = a.callback, d = a.action, f = a.persistent, e = a.allowIn, a = a.combo), b instanceof Function ? (d = c, c = b, b = "$$undefined$$") : angular.isUndefined(b) && (b = "$$undefined$$"), void 0 === f && (f = !0), "function" == typeof c) {
                        h = c, e instanceof Array || (e = []);
                        for (var k, l = 0;
                             l < e.length;
                             l++) e[l] = e[l].toUpperCase(), k = i.indexOf(e[l]), -1 !== k && i.splice(k, 1);
                        c = function (a) {
                            var b = !0, c = a.target || a.srcElement, d = c.nodeName.toUpperCase();
                            if ((" " + c.className + " ").indexOf(" mousetrap ") > -1) b = !0;
                            else for (var e = 0;
                                      e < i.length;
                                      e++) if (i[e] === d) {
                                    b = !1;
                                    break
                                }
                            b && n(h.apply(this,
                                arguments))
                        }
                    }
                    "string" == typeof d ? Mousetrap.bind(a,
                        n(c),
                        d) : Mousetrap.bind(a,
                        n(c));
                    var m = new g(a,
                        b,
                        c,
                        d,
                        e,
                        f);
                    return o.hotkeys.push(m),
                        m
                }

                function k(a) {
                    var b = a instanceof g ? a.combo : a;
                    if (Mousetrap.unbind(b), b instanceof Array) {
                        for (var c = !0, d = 0;
                             d < b.length;
                             d++) c = k(b[d]) && c;
                        return c
                    }
                    var e = o.hotkeys.indexOf(l(b));
                    return e > -1 ? (o.hotkeys[e].combo.length > 1 ? o.hotkeys[e].combo.splice(o.hotkeys[e].combo.indexOf(b),
                        1) : o.hotkeys.splice(e,
                        1),
                        !0) : !1
                }

                function l(a) {
                    for (var b, c = 0;
                         c < o.hotkeys.length;
                         c++) if (b = o.hotkeys[c], b.combo.indexOf(a) > -1) return b;
                    return !1
                }

                function m(a) {
                    return p[a.$id] = [], a.$on("$destroy", function () {
                            for (var b = p[a.$id].length;
                                 b--;
                            ) k(p[a.$id][b]), delete p[a.$id][b]
                        }
                    ),
                        {
                            add: function (b) {
                                var c;
                                return c = arguments.length > 1 ? j.apply(this, arguments) : j(b), p[a.$id].push(c), this
                            }
                        }
                }

                function n(a) {
                    return function (c, d) {
                        if (a instanceof Array) {
                            var e = a[0], f = a[1];
                            a = function () {
                                f.scope.$eval(e)
                            }
                        }
                        b.$apply(function () {
                                a(c, l(d))
                            }
                        )
                    }
                }

                Mousetrap.stopCallback = function (a,
                                                   b) {
                    return (" " + b.className + " ").indexOf(" mousetrap ") > -1 ? !1 : b.contentEditable && "true" == b.contentEditable
                }
                    ,
                    g.prototype.format = function () {
                        for (var a = this.combo[0], b = a.split(/[\s]/), c = 0;
                             c < b.length;
                             c++) b[c] = f(b[c]);
                        return b
                    }
                ;
                var o = b.$new();
                o.hotkeys = [],
                    o.helpVisible = !1,
                    o.title = this.templateTitle,
                    o.toggleCheatSheet = i;
                var p = [];
                if (b.$on("$routeChangeSuccess",
                    function (a,
                              b) {
                        h(), b && b.hotkeys && angular.forEach(b.hotkeys, function (a) {
                                var c = a[2];
                                ("string" == typeof c || c instanceof String) && (a[2] = [c, b]), a[5] = !1, j.apply(this, a)
                            }
                        )
                    }
                ),
                    this.includeCheatSheet) {
                    var q = e[0], r = a[0], s = angular.element(this.template);
                    j(this.cheatSheetHotkey, this.cheatSheetDescription, i), (r === q || r === q.documentElement) && (r = q.body), angular.element(r).append(c(s)(o))
                }
                var t = !1,
                    u = {
                        add: j,
                        del: k,
                        get: l,
                        bindTo: m,
                        template: this.template,
                        toggleCheatSheet: i,
                        includeCheatSheet: this.includeCheatSheet,
                        cheatSheetHotkey: this.cheatSheetHotkey,
                        cheatSheetDescription: this.cheatSheetDescription,
                        purgeHotkeys: h,
                        templateTitle: this.templateTitle
                    }
                ;
                return u
            }
            ]
        }
    ).directive("hotkey",
        ["hotkeys",
            function (a) {
                return {
                    restrict: "A", link: function (b, c, d) {
                        var e, f;
                        angular.forEach(b.$eval(d.hotkey), function (b, c) {
                                f = "string" == typeof d.hotkeyAllowIn ? d.hotkeyAllowIn.split(/[\s, ]+/) : [], e = c, a.add({
                                        combo: c,
                                        description: d.hotkeyDescription,
                                        callback: b,
                                        action: d.hotkeyAction,
                                        allowIn: f
                                    }
                                )
                            }
                        ),
                            c.bind("$destroy",
                                function () {
                                    a.del(e)
                                }
                            )
                    }
                }
            }
        ]).run(["hotkeys",
        function () {
        }])
}
(),
    function (a,
              b) {
        function c(a, b, c) {
            return a.addEventListener ? void a.addEventListener(b, c, !1) : void a.attachEvent("on" + b, c)
        }

        function d(a) {
            if ("keypress" == a.type) {
                var b = String.fromCharCode(a.which);
                return a.shiftKey || (b = b.toLowerCase()), b
            }
            return y[a.which] ? y[a.which] : z[a.which] ? z[a.which] : String.fromCharCode(a.which).toLowerCase()
        }

        function e(a,
                   b) {
            return a.sort().join(",") === b.sort().join(",")
        }

        function f(a) {
            a = a || {};
            var b, c = !1;
            for (b in E) a[b] ? c = !0 : E[b] = 0;
            c || (H = !1)
        }

        function g(a,
                   b,
                   c,
                   d,
                   f,
                   g) {
            var h, i, j = [], k = c.type;
            if (!C[a]) return [];
            for ("keyup" == k && n(a) && (b = [a]), h = 0;
                 h < C[a].length;
                 ++h) if (i = C[a][h], (d || !i.seq || E[i.seq] == i.level) && k == i.action && ("keypress" == k && !c.metaKey && !c.ctrlKey || e(b, i.modifiers))) {
                var l = !d && i.combo == f, m = d && i.seq == d && i.level == g;
                (l || m) && C[a].splice(h, 1), j.push(i)
            }
            return j
        }

        function h(a) {
            var b = [];
            return a.shiftKey && b.push("shift"), a.altKey && b.push("alt"), a.ctrlKey && b.push("ctrl"), a.metaKey && b.push("meta"), b
        }

        function i(a) {
            return a.preventDefault ? void a.preventDefault() : void (a.returnValue = !1)
        }

        function j(a) {
            return a.stopPropagation ? void a.stopPropagation() : void (a.cancelBubble = !0)
        }

        function k(a,
                   b,
                   c,
                   d) {
            J.stopCallback(b, b.target || b.srcElement, c, d) || a(b, c) === !1 && (i(b), j(b))
        }

        function l(a,
                   b,
                   c) {
            var d, e = g(a, b, c), h = {}, i = 0, j = !1;
            for (d = 0;
                 d < e.length;
                 ++d) e[d].seq && (i = Math.max(i, e[d].level));
            for (d = 0;
                 d < e.length;
                 ++d) if (e[d].seq) {
                if (e[d].level != i) continue;
                j = !0, h[e[d].seq] = 1, k(e[d].callback, c, e[d].combo, e[d].seq)
            } else j || k(e[d].callback,
                c,
                e[d].combo);
            var l = "keypress" == c.type && G;
            c.type != H || n(a) || l || f(h),
                G = j && "keydown" == c.type
        }

        function m(a) {
            "number" != typeof a.which && (a.which = a.keyCode);
            var b = d(a);
            if (b) return "keyup" == a.type && F === b ? void (F = !1) : void J.handleKey(b, h(a), a)
        }

        function n(a) {
            return "shift" == a || "ctrl" == a || "alt" == a || "meta" == a
        }

        function o() {
            clearTimeout(x), x = setTimeout(f, 1e3)
        }

        function p() {
            if (!w) {
                w = {};
                for (var a in y) a > 95 && 112 > a || y.hasOwnProperty(a) && (w[y[a]] = a)
            }
            return w
        }

        function q(a,
                   b,
                   c) {
            return c || (c = p()[a] ? "keydown" : "keypress"), "keypress" == c && b.length && (c = "keydown"), c
        }

        function r(a,
                   b,
                   c,
                   e) {
            function g(b) {
                return function () {
                    H = b, ++E[a], o()
                }
            }

            function h(b) {
                k(c, b, a), "keyup" !== e && (F = d(b)), setTimeout(f, 10)
            }

            E[a] = 0;
            for (var i = 0;
                 i < b.length;
                 ++i) {
                var j = i + 1 === b.length, l = j ? h : g(e || t(b[i + 1]).action);
                u(b[i], l, e, a, i)
            }
        }

        function s(a) {
            return "+" === a ? ["+"] : a.split("+")
        }

        function t(a,
                   b) {
            var c, d, e, f = [];
            for (c = s(a), e = 0;
                 e < c.length;
                 ++e) d = c[e], B[d] && (d = B[d]), b && "keypress" != b && A[d] && (d = A[d], f.push("shift")), n(d) && f.push(d);
            return b = q(d, f, b), {
                key: d, modifiers: f, action: b
            }
        }

        function u(a,
                   b,
                   c,
                   d,
                   e) {
            D[a + ":" + c] = b, a = a.replace(/\s+/g, " ");
            var f, h = a.split(" ");
            return h.length > 1 ? void r(a, h, b, c) : (f = t(a, c), C[f.key] = C[f.key] || [], g(f.key, f.modifiers, {
                    type: f.action
                }
                ,
                d,
                a,
                e),
                void C[f.key][d ? "unshift" : "push"]({
                        callback: b, modifiers: f.modifiers, action: f.action, seq: d, level: e, combo: a
                    }
                ))
        }

        function v(a,
                   b,
                   c) {
            for (var d = 0;
                 d < a.length;
                 ++d) u(a[d], b, c)
        }

        for (var w,
                 x,
                 y = {
                     8: "backspace",
                     9: "tab",
                     13: "enter",
                     16: "shift",
                     17: "ctrl",
                     18: "alt",
                     20: "capslock",
                     27: "esc",
                     32: "space",
                     33: "pageup",
                     34: "pagedown",
                     35: "end",
                     36: "home",
                     37: "left",
                     38: "up",
                     39: "right",
                     40: "down",
                     45: "ins",
                     46: "del",
                     91: "meta",
                     93: "meta",
                     224: "meta"
                 }
                 ,
                 z = {
                     106: "*",
                     107: "+",
                     109: "-",
                     110: ".",
                     111: "/",
                     186: ";",
                     187: "=",
                     188: ",",
                     189: "-",
                     190: ".",
                     191: "/",
                     192: "`",
                     219: "[",
                     220: "\\",
                     221: "]",
                     222: "'"
                 }
                 ,
                 A = {
                     "~": "`",
                     "!": "1",
                     "@": "2",
                     "#": "3",
                     $: "4",
                     "%": "5",
                     "^": "6",
                     "&": "7",
                     "*": "8",
                     "(": "9",
                     ")": "0",
                     _: "-",
                     "+": "=",
                     ":": ";",
                     '"': "'",
                     "<": ",",
                     ">": ".",
                     "?": "/",
                     "|": "\\"
                 }
                 ,
                 B = {
                     option: "alt",
                     command: "meta",
                     "return": "enter",
                     escape: "esc",
                     mod: /Mac|iPod|iPhone|iPad/.test(navigator.platform) ? "meta" : "ctrl"
                 }
                 ,
                 C = {},
                 D = {},
                 E = {},
                 F = !1,
                 G = !1,
                 H = !1,
                 I = 1;
             20 > I;
             ++I) y[111 + I] = "f" + I;
        for (I = 0;
             9 >= I;
             ++I) y[I + 96] = I;
        c(b,
            "keypress",
            m),
            c(b,
                "keydown",
                m),
            c(b,
                "keyup",
                m);
        var J = {
                bind: function (a, b, c) {
                    return a = a instanceof Array ? a : [a], v(a, b, c), this
                }
                ,
                unbind: function (a,
                                  b) {
                    return J.bind(a, function () {
                    }, b)
                }
                ,
                trigger: function (a,
                                   b) {
                    return D[a + ":" + b] && D[a + ":" + b]({}, a), this
                }
                ,
                reset: function () {
                    return C = {}, D = {}, this
                }
                ,
                stopCallback: function (a,
                                        b) {
                    return (" " + b.className + " ").indexOf(" mousetrap ") > -1 ? !1 : "INPUT" == b.tagName || "SELECT" == b.tagName || "TEXTAREA" == b.tagName || b.isContentEditable
                }
                ,
                handleKey: l
            }
        ;
        a.Mousetrap = J,
        "function" == typeof define && define.amd && define(J)
    }
    (window,
        document);