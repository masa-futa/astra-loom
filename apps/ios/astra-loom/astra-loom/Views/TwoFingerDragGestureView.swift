import SwiftUI
import UIKit

/// タッチを通過させるUIView
class PassThroughView: UIView {
    override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
        let view = super.hitTest(point, with: event)
        return view == self ? nil : view
    }
}

/// 2本指ドラッグジェスチャーを検出するビュー
struct TwoFingerDragGestureView: UIViewRepresentable {
    let onChanged: (CGFloat) -> Void
    let onEnded: () -> Void

    func makeUIView(context: Context) -> PassThroughView {
        let view = PassThroughView()
        view.backgroundColor = .clear

        let panGesture = UIPanGestureRecognizer(
            target: context.coordinator,
            action: #selector(Coordinator.handlePan(_:))
        )
        panGesture.minimumNumberOfTouches = 2
        panGesture.maximumNumberOfTouches = 2
        panGesture.delegate = context.coordinator

        view.addGestureRecognizer(panGesture)
        return view
    }

    func updateUIView(_ uiView: PassThroughView, context: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(onChanged: onChanged, onEnded: onEnded)
    }

    class Coordinator: NSObject, UIGestureRecognizerDelegate {
        let onChanged: (CGFloat) -> Void
        let onEnded: () -> Void
        var lastTranslation: CGFloat = 0

        init(onChanged: @escaping (CGFloat) -> Void, onEnded: @escaping () -> Void) {
            self.onChanged = onChanged
            self.onEnded = onEnded
        }

        @objc func handlePan(_ gesture: UIPanGestureRecognizer) {
            let translation = gesture.translation(in: gesture.view).x

            switch gesture.state {
            case .began:
                lastTranslation = 0

            case .changed:
                let delta = translation - lastTranslation
                onChanged(delta)
                lastTranslation = translation

            case .ended, .cancelled:
                lastTranslation = 0
                onEnded()

            default:
                break
            }
        }

        // 他のジェスチャーと共存させる
        func gestureRecognizer(
            _ gestureRecognizer: UIGestureRecognizer,
            shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer
        ) -> Bool {
            return true
        }

        // 2本指の時のみジェスチャーを受け付ける
        func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
            // このタッチを受け付けるかどうかは、全体のタッチ数で判断する必要があるため、
            // ジェスチャー開始時にチェックする
            return true
        }

        func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
            // 2本指の時のみジェスチャーを開始
            guard let panGesture = gestureRecognizer as? UIPanGestureRecognizer else {
                return false
            }
            return panGesture.numberOfTouches == 2
        }
    }
}
